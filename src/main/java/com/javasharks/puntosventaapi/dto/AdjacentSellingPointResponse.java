package com.javasharks.puntosventaapi.dto;

import java.util.Map;

/**
 * DTO para responder con los puntos de venta adyacentes a un punto dado.
 */
public record AdjacentSellingPointResponse(
    Long puntoVentaId,
    String nombrePuntoVenta,
    Map<Long, AdjacencyInfo> adyacencias
) {
    public record AdjacencyInfo(
        String nombrePuntoVenta,
        Double costo
    ) {}
}
