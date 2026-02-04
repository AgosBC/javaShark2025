package com.javasharks.puntosventaapi.service;

import com.javasharks.puntosventaapi.dto.AcreditacionRequest;
import com.javasharks.puntosventaapi.dto.AcreditacionResponse;

import java.util.List;

public interface AccreditationService {

    AcreditacionResponse procesarAcreditacion(AcreditacionRequest request);

    List<AcreditacionResponse> getAllAcreditaciones();

    List<AcreditacionResponse> getAcreditacionesByPuntoVenta(Long puntoVentaId);

}
