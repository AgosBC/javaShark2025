package com.javasharks.puntosventaapi.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;


public record Charge(
    @NotNull(message = "El ID del punto de venta de origen no puede ser nulo")
    Long idSellingPointOrigin,
    
    @NotNull(message = "El ID del punto de venta destino no puede ser nulo")
    Long idSellingPointDestination,
    
    @NotNull(message = "El value no puede ser nulo")
    @PositiveOrZero(message = "El value debe ser mayor o igual a 0")
    Double value
) {
    /**
     * Constructor compacto para normalizar la representación
     * (asegura que idA <= idB para evitar duplicados) y errores al buscar costos
     */
    //to-do validar
    public Charge {
        if (idSellingPointOrigin != null && idSellingPointDestination != null && idSellingPointOrigin > idSellingPointDestination) {
            Long temp = idSellingPointOrigin;
            idSellingPointOrigin = idSellingPointDestination;
            idSellingPointDestination = temp;
        }
    }
}
