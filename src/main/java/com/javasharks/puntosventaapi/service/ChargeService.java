package com.javasharks.puntosventaapi.service;

import com.javasharks.puntosventaapi.dto.AdjacentSellingPointResponse;
import com.javasharks.puntosventaapi.dto.MinDistanceResponse;
import com.javasharks.puntosventaapi.model.Charge;

import java.util.List;

public interface ChargeService {

    Charge addCharge(Charge charge);

    void removeCharge(Long origenId, Long destinoId);

    AdjacentSellingPointResponse getAdjacent(Long idPuntoVenta);

    MinDistanceResponse calculateMinDistance(Long origenId, Long destinoId);

    void init(List<Charge> charges);
}
