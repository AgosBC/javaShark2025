package com.javasharks.puntosventaapi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respuestas de acreditación.
 */
public record AcreditacionResponse(
        Long id,
        BigDecimal importe,
        Long puntoVentaId,
        String nombrePuntoVenta,
        LocalDateTime fechaRecepcion
) {
}
