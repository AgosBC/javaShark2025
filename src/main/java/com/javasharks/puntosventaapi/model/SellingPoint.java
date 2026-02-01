package com.javasharks.puntosventaapi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SellingPoint(
        @NotNull(message = "El ID no puede ser nulo")
        @Positive(message = "El ID debe ser positivo")
        Long id,

        @NotBlank(message = "El nombre no puede estar vacío")
        String nombre
) {

    public SellingPoint {
        if (nombre != null) {
            nombre = nombre.trim();
        }
    }
}
