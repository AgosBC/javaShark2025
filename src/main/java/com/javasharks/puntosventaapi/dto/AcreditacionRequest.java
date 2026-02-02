package com.javasharks.puntosventaapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record AcreditacionRequest(
        @NotNull(message = "El importe no puede ser nulo")
        @Positive(message = "El importe debe ser positivo")
        BigDecimal importe,

        @NotNull(message = "El ID del punto de venta no puede ser nulo")
        @PositiveOrZero(message = "El ID del punto de venta debe ser mayor o igual a 0")
        Long puntoVentaId
) {
}
