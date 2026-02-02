package com.javasharks.puntosventaapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories
public class PuntosVentaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PuntosVentaApiApplication.class, args);
    }
}
