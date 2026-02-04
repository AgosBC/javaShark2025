package com.javasharks.puntosventaapi.service;

import com.javasharks.puntosventaapi.model.SellingPoint;

import java.util.List;
import java.util.Optional;

public interface SellingPointService {

    void initializeCache(List<SellingPoint> sellingPoint);

    List<SellingPoint> findAll();

    Optional<SellingPoint> findById(Long id);

    SellingPoint create(SellingPoint sellingPoint);

    SellingPoint update(Long id, SellingPoint sellingPoint);

    void delete(Long id);

    boolean exists(Long id);
}
