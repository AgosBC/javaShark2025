package com.javasharks.puntosventaapi.dto;

import java.util.List;

/**
 * DTO que representa el camino mínimo entre dos puntos de venta.
 * Incluye el value total y la lista de puntos en el camino.
 */
public record MinDistanceResponse(
    Long puntoVentaOrigenId,
    String puntoVentaOrigenNombre,
    Long puntoVentaDestinoId,
    String puntoVentaDestinoNombre,
    Double costoTotal,
    List<String> camino,
    boolean alcanzable
) {
    public static MinDistanceResponse inalcanzable(Long origenId, String origenNombre, Long destinoId, String destinoNombre) {
        return new MinDistanceResponse(
            origenId, origenNombre, 
            destinoId, destinoNombre,
            null,
            List.of(),
            false
        );
    }
}
