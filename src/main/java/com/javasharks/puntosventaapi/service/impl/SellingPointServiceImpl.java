package com.javasharks.puntosventaapi.service.impl;

import com.javasharks.puntosventaapi.exception.ResourceNotFoundException;
import com.javasharks.puntosventaapi.model.SellingPoint;
import com.javasharks.puntosventaapi.service.SellingPointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class SellingPointServiceImpl implements SellingPointService {

    private static final Logger log = LoggerFactory.getLogger(SellingPointServiceImpl.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CACHE_KEY_PREFIX = "sellingPoint:";
    private static final String ALL_CACHE_KEY = "sellingPoint:all";

    public SellingPointServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    @CacheEvict(value = "sellingPoint", allEntries = true)
    public void initializeCache(List<SellingPoint> sellingPoint) {
        // Limpiar cache existente
        clear();

        // Cargar todos los puntos de venta en Redis
        sellingPoint.forEach(pv -> redisTemplate.opsForValue().set(
                CACHE_KEY_PREFIX + pv.id(),
                pv,
                24,
                TimeUnit.HOURS
        ));

        log.info("Caché Redis inicializado con {} puntos de venta", sellingPoint.size());
    }

    @Override
    @Cacheable(value = "sellingPoint", key = "'all'")
    public List<SellingPoint> findAll() {
        log.debug("Recuperando todos los puntos de venta desde Redis");
        Set<String> keys = redisTemplate.keys(CACHE_KEY_PREFIX + "*");
        if (keys.isEmpty()) {
            return new ArrayList<>();
        }

        List<SellingPoint> sellingPoint = new ArrayList<>();
        for (String key : keys) {
            if (!key.equals(ALL_CACHE_KEY)) {
                Object value = redisTemplate.opsForValue().get(key);
                if (value instanceof SellingPoint pv) {
                    sellingPoint.add(pv);
                }
            }
        }

        log.debug("Total puntos de venta recuperados: {}", sellingPoint.size());
        return sellingPoint.stream()
                .sorted(Comparator.comparingLong(SellingPoint::id))
                .toList();
    }

    @Override
    public Optional<SellingPoint> findById(Long id) {
        log.debug("Buscando punto de venta con ID: {}", id);
        Object value = redisTemplate.opsForValue().get(CACHE_KEY_PREFIX + id);
        if (value instanceof SellingPoint pv) {
            return Optional.of(pv);
        }
        return Optional.empty();
    }

    /**
     * Crea un nuevo punto de venta en Redis.
     * Put actualiza el caché automáticamente.
     */
    @Caching(
            put = @CachePut(value = "sellingPoint", key = "#sellingPoint.id()"),
            evict = @CacheEvict(value = "sellingPoint", key = "'all'")
    )
    @Override
    public SellingPoint create(SellingPoint sellingPoint) {
        // Verificar si ya existe
        if (redisTemplate.hasKey(CACHE_KEY_PREFIX + sellingPoint.id())) {
            throw new IllegalArgumentException(
                    String.format("Ya existe un punto de venta con ID %d", sellingPoint.id())
            );
        }

        redisTemplate.opsForValue().set(
                CACHE_KEY_PREFIX + sellingPoint.id(),
                sellingPoint,
                24,
                TimeUnit.HOURS
        );

        log.info("Punto de venta creado en Redis: {}", sellingPoint);
        return sellingPoint;
    }

    @Override
    @Caching(
            put = @CachePut(value = "sellingPoint", key = "#sellingPoint.id()"),
            evict = @CacheEvict(value = "sellingPoint", key = "'all'")
    )
    public SellingPoint update(Long id, SellingPoint sellingPoint) {
        if (!redisTemplate.hasKey(CACHE_KEY_PREFIX + id)) {
            throw new ResourceNotFoundException(
                    String.format("Punto de venta con ID %d no encontrado", id)
            );
        }

        SellingPoint updated = new SellingPoint(id, sellingPoint.nombre());
        redisTemplate.opsForValue().set(
                CACHE_KEY_PREFIX + id,
                updated,
                24,
                TimeUnit.HOURS
        );

        log.info("Punto de venta actualizado en Redis: {}", updated);
        return updated;
    }

    @Caching(evict = {
            @CacheEvict(value = "sellingPoint", key = "#id"),
            @CacheEvict(value = "sellingPoint", key = "'all'")
    }
    )
    @Override
    public void delete(Long id) {
        String key = CACHE_KEY_PREFIX + id;
        Object removed = redisTemplate.opsForValue().get(key);

        if (removed == null) {
            throw new ResourceNotFoundException(
                    String.format("Punto de venta con ID %d no encontrado", id)
            );
        }

        redisTemplate.delete(key);
        log.info("Punto de venta eliminado de Redis: {}", removed);
    }

    @Override
    public boolean exists(Long id) {
        return redisTemplate.hasKey(CACHE_KEY_PREFIX + id);
    }

    /**
     * Limpia el caché de Redis
     */
    @CacheEvict(value = "sellingPoint", allEntries = true)
    private void clear() {
        Set<String> keys = redisTemplate.keys(CACHE_KEY_PREFIX + "*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        log.warn("Caché de puntos de venta en Redis limpiado");
    }
}
