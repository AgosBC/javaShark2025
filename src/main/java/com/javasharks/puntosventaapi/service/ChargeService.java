package com.javasharks.puntosventaapi.service;

import com.javasharks.puntosventaapi.dto.MinDistanceResponse;
import com.javasharks.puntosventaapi.dto.AdjacentSellingPointResponse;
import com.javasharks.puntosventaapi.exception.ResourceNotFoundException;
import com.javasharks.puntosventaapi.model.Charge;
import com.javasharks.puntosventaapi.model.SellingPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.javasharks.puntosventaapi.config.RedisConfig.DEFAULT_TTL;

@Service
public class ChargeService {

    private static final Logger log = LoggerFactory.getLogger(ChargeService.class);
    private final SellingPointService sellingPointService;
    private final RedisTemplate<String, Object> redisTemplate;

    // Redis keys para el grafo de costos
    private static final String COSTO_KEY_PREFIX = "costo:";

    public ChargeService(SellingPointService sellingPointService, RedisTemplate<String, Object> redisTemplate) {
        this.sellingPointService = sellingPointService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Carga un nuevo costo entre dos puntos de venta en Redis.
     * Crea un camino directo bidireccional.
     */
    public Charge addCharge(Charge charge) {
        // Validar que ambos puntos de venta existan
        if (!sellingPointService.exists(charge.idSellingPointOrigin())) {
            throw new ResourceNotFoundException(
                    String.format("Punto de venta con ID %d no existe", charge.idSellingPointOrigin())
            );
        }
        if (!sellingPointService.exists(charge.idSellingPointDestination())) {
            throw new ResourceNotFoundException(
                    String.format("Punto de venta con ID %d no existe", charge.idSellingPointDestination())
            );
        }

        // Agregar arista bidireccional en Redis usando Hash
        String keyA = COSTO_KEY_PREFIX + charge.idSellingPointOrigin();
        String keyB = COSTO_KEY_PREFIX + charge.idSellingPointDestination();

        redisTemplate.opsForHash().put(keyA, charge.idSellingPointDestination().toString(), charge.value());
        redisTemplate.opsForHash().put(keyB, charge.idSellingPointOrigin().toString(), charge.value());

        // Establecer TTL de 24 horas
        redisTemplate.expire(keyA, DEFAULT_TTL.toHours(), TimeUnit.HOURS);
        redisTemplate.expire(keyB, DEFAULT_TTL.toHours(), TimeUnit.HOURS);

        log.info("Costo agregado a Redis: {} <-> {} = {}",
                charge.idSellingPointOrigin(), charge.idSellingPointDestination(), charge.value());

        return charge;
    }

    /**
     * Remueve el value entre dos puntos de venta de Redis.
     */
    public void removeCharge(Long idA, Long idB) {
        String keyA = COSTO_KEY_PREFIX + idA;
        String keyB = COSTO_KEY_PREFIX + idB;

        Long removed = redisTemplate.opsForHash().delete(keyA, idB.toString());
        redisTemplate.opsForHash().delete(keyB, idA.toString());

        if (removed == 0) {
            throw new ResourceNotFoundException(
                    String.format("No existe conexión directa entre puntos %d y %d", idA, idB)
            );
        }

        log.info("Costo removido de Redis entre {} y {}", idA, idB);
    }

    /**
     * Consulta los puntos de venta directamente conectados a un punto dado desde Redis.
     */
    public AdjacentSellingPointResponse getAdjacent(Long idPuntoVenta) {
        SellingPoint sellingPoint = sellingPointService.findById(idPuntoVenta)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Punto de venta con ID %d no encontrado", idPuntoVenta)
                ));

        String key = COSTO_KEY_PREFIX + idPuntoVenta;
        Map<Object, Object> adjacent = redisTemplate.opsForHash().entries(key);
        Map<Long, AdjacentSellingPointResponse.AdjacencyInfo> adjacency = new HashMap<>();

        adjacent.forEach((idObj, costObj) -> {
            Long adjacentId = Long.parseLong(idObj.toString());
            Double cost = ((Number) costObj).doubleValue();

            sellingPointService.findById(adjacentId).ifPresent(a -> {
                adjacency.put(adjacentId,
                        new AdjacentSellingPointResponse.AdjacencyInfo(a.nombre(), cost));
            });
        });

        return new AdjacentSellingPointResponse(
                sellingPoint.id(),
                sellingPoint.nombre(),
                adjacency
        );
    }

    /**
     * Calcula el camino con value mínimo entre dos puntos usando el algoritmo de Dijkstra.
     * Lee el grafo desde Redis para el cálculo.
     * <p>
     * Algoritmo de Dijkstra:
     * - Garantiza encontrar el camino más corto en grafos con pesos positivos
     * -
     */
    public MinDistanceResponse calculateMinDistance(Long origenId, Long destinoId) {
        // Validar que ambos puntos existan
        SellingPoint origin = sellingPointService.findById(origenId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Punto de venta origen con ID %d no encontrado", origenId)
                ));

        SellingPoint destino = sellingPointService.findById(destinoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Punto de venta destino con ID %d no encontrado", destinoId)
                ));

        // mismo origen y destino = sin costo
        if (origenId.equals(destinoId)) {
            return new MinDistanceResponse(
                    origenId, origin.nombre(),
                    destinoId, destino.nombre(),
                    0.0,
                    List.of(origin.nombre()),
                    true
            );
        }

        // Ejecutar Dijkstra
        DijkstraResult result = dijkstra(origenId, destinoId);

        if (!result.alcanzable) {
            return MinDistanceResponse.inalcanzable(
                    origenId, origin.nombre(),
                    destinoId, destino.nombre()
            );
        }

        // Reconstruir el camino con nombres
        List<String> caminoNombres = new ArrayList<>();
        for (Long id : result.camino) {
            sellingPointService.findById(id)
                    .ifPresent(pv -> caminoNombres.add(pv.nombre()));
        }

        return new MinDistanceResponse(
                origenId, origin.nombre(),
                destinoId, destino.nombre(),
                result.costoTotal,
                caminoNombres,
                true
        );
    }

    /**
     * Implementación del algoritmo de Dijkstra.
     * Lee el grafo desde Redis para el cálculo.
     */
    private DijkstraResult dijkstra(Long origen, Long destino) {
        Map<Long, Double> distances = new HashMap<>();
        Map<Long, Long> predecessors = new HashMap<>();
        Set<Long> checked = new HashSet<>();

        // PriorityQueue para procesar nodos por menor distancia
        PriorityQueue<NodoDistancia> queue = new PriorityQueue<>(
                Comparator.comparingDouble(NodoDistancia::distancia)
        );

        // Inicializar distancias
        distances.put(origen, 0.0);
        queue.offer(new NodoDistancia(origen, 0.0));

        while (!queue.isEmpty()) {
            NodoDistancia actual = queue.poll();
            Long nodoActual = actual.nodoId();

            if (checked.contains(nodoActual)) {
                continue;
            }

            checked.add(nodoActual);

            // Si llegamos al destino, podemos terminar
            if (nodoActual.equals(destino)) {
                break;
            }

            // Explorar vecinos desde Redis
            String key = COSTO_KEY_PREFIX + nodoActual;
            Map<Object, Object> adjacentData = redisTemplate.opsForHash().entries(key);
            Map<Long, Double> adjacents = new HashMap<>();
            adjacentData.forEach((k, v) -> {
                adjacents.put(Long.parseLong(k.toString()), ((Number) v).doubleValue());
            });

            for (Map.Entry<Long, Double> adjacent : adjacents.entrySet()) {
                Long adjacentId = adjacent.getKey();
                Double cost = adjacent.getValue();

                if (checked.contains(adjacentId)) {
                    continue;
                }

                double newDistance = distances.get(nodoActual) + cost;
                double currentDistance = distances.getOrDefault(adjacentId, Double.POSITIVE_INFINITY);

                if (newDistance < currentDistance) {
                    distances.put(adjacentId, newDistance);
                    predecessors.put(adjacentId, nodoActual);
                    queue.offer(new NodoDistancia(adjacentId, newDistance));
                }
            }
        }

        // Reconstruir camino
        if (!distances.containsKey(destino) || distances.get(destino) == Double.POSITIVE_INFINITY) {
            return new DijkstraResult(false, Double.POSITIVE_INFINITY, List.of());
        }

        List<Long> camino = new ArrayList<>();
        Long actual = destino;

        while (actual != null) {
            camino.add(actual);
            actual = predecessors.get(actual);
        }

        Collections.reverse(camino);

        return new DijkstraResult(true, distances.get(destino), camino);
    }

    /**
     * Inicializa el grafo en Redis con costos predefinidos.
     * Usado por DataInitializer al inicio de la aplicación.
     */
    public void init(List<Charge> charges) {
        clear();
        charges.forEach(this::addCharge);
        log.info("Grafo inicializado en Redis con {} conexiones", charges.size());
    }

    /**
     * Limpia el grafo de Redis (útil para testing).
     */
    public void clear() {
        Set<String> keys = redisTemplate.keys(COSTO_KEY_PREFIX + "*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        log.warn("Grafo de costos en Redis limpiado");
    }

    // Records auxiliares
    //TO-DO ingles
    private record NodoDistancia(Long nodoId, Double distancia) {
    }

    /**
     * Record para el resultado del algoritmo de Dijkstra.
     */
    private record DijkstraResult(boolean alcanzable, Double costoTotal, List<Long> camino) {
    }
}
