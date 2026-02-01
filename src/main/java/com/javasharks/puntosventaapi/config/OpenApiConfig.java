package com.javasharks.puntosventaapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI/Swagger para documentación de la API.
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Puntos de Venta API")
                .version("1.0.0")
                .description("""
                    API REST para gestión de puntos de venta, costos entre puntos y acreditaciones.
                    
                    Características:
                    - Caché en memoria thread-safe para puntos de venta
                    - Grafo de charges con algoritmo de Dijkstra para camino mínimo
                    - Persistencia de acreditaciones en PostgreSQL
                    
                    Tecnologías:
                    - Java 21
                    - Spring Boot 3.2
                    - PostgreSQL
                    - Redis
                    - Docker
                    """));
    }
}
