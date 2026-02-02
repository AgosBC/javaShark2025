package com.javasharks.puntosventaapi.config;

import com.javasharks.puntosventaapi.model.Charge;
import com.javasharks.puntosventaapi.model.SellingPoint;
import com.javasharks.puntosventaapi.service.ChargeService;
import com.javasharks.puntosventaapi.service.SellingPointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración para inicializar datos en Redis al arranque de la aplicación.
 *
 * Utiliza ApplicationRunner de Spring Boot para ejecutar código al inicio.
 * 
 * Precarga:
 * - Puntos de venta iniciales en Redis
 * - Grafo de costos en Redis
 * 
 * Beneficio: Los datos están disponibles inmediatamente en el caché distribuido,
 * accesibles por todas las instancias de la aplicación.
 */
@Configuration
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final SellingPointService sellingPointService;
    private final ChargeService chargeService;

    public DataInitializer(SellingPointService sellingPointService, ChargeService chargeService) {
        this.sellingPointService = sellingPointService;
        this.chargeService = chargeService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("=== Iniciando carga de datos iniciales en Redis ===");
        // Inicializar Puntos de Venta en Redis
        initializePuntosVenta();

        // Inicializar Costos en Redis
        initializeCostos();

        log.info("=== Datos iniciales cargados exitosamente en Redis ===");
    }

    private void initializePuntosVenta() {
        List<SellingPoint> puntosVenta = List.of(
            new SellingPoint(1L, "CABA"),
            new SellingPoint(2L, "GBA_1"),
            new SellingPoint(3L, "GBA_2"),
            new SellingPoint(4L, "Santa Fe"),
            new SellingPoint(5L, "Córdoba"),
            new SellingPoint(6L, "Misiones"),
            new SellingPoint(7L, "Salta"),
            new SellingPoint(8L, "Chubut"),
            new SellingPoint(9L, "Santa Cruz"),
            new SellingPoint(10L, "Catamarca")
        );
        
        sellingPointService.initializeCache(puntosVenta);
        log.info("Inicializados {} puntos de venta", puntosVenta.size());
    }


    private void initializeCostos() {
        List<Charge> charges = List.of(
            new Charge(1L, 2L, 2.0),
            new Charge(1L, 3L, 3.0),
            new Charge(2L, 3L, 5.0),
            new Charge(2L, 4L, 10.0),
            new Charge(1L, 4L, 11.0),
            new Charge(4L, 5L, 5.0),
            new Charge(2L, 5L, 14.0),
            new Charge(6L, 7L, 32.0),
            new Charge(8L, 9L, 11.0),
            new Charge(10L, 7L, 5.0),
            new Charge(3L, 8L, 10.0),
            new Charge(5L, 8L, 30.0),
            new Charge(10L, 5L, 5.0),
            new Charge(4L, 6L, 6.0)
        );

        chargeService.init(charges);
        log.info("Inicializadas {} conexiones de costos", charges.size());
    }
}
