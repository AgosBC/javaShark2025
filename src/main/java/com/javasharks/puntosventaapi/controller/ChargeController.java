package com.javasharks.puntosventaapi.controller;

import com.javasharks.puntosventaapi.dto.MinDistanceResponse;
import com.javasharks.puntosventaapi.dto.AdjacentSellingPointResponse;
import com.javasharks.puntosventaapi.model.Charge;
import com.javasharks.puntosventaapi.service.ChargeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/costos")
@Tag(name = "Costos", description = "API para gestión de costos entre puntos de venta")
public class ChargeController {

    private final ChargeService chargeService;

    public ChargeController(ChargeService chargeService) {
        this.chargeService = chargeService;
    }

    @PostMapping
    @Operation(summary = "Cargar un nuevo costo entre dos puntos de venta")
    public ResponseEntity<Charge> addCharge(@Valid @RequestBody Charge charge) {
        Charge created = chargeService.addCharge(charge);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @DeleteMapping
    @Operation(summary = "Remover un costo entre dos puntos de venta")
    public ResponseEntity<Void> removeCharge(
            @RequestParam Long idA,
            @RequestParam Long idB) {
        chargeService.removeCharge(idA, idB);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/adyacentes/{id}")
    @Operation(summary = "Consultar puntos de venta adyacentes a un punto dado")
    public ResponseEntity<AdjacentSellingPointResponse> getAdjacent(@PathVariable Long id) {
        AdjacentSellingPointResponse response = chargeService.getAdjacent(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/camino-minimo")
    @Operation(summary = "Calcular el camino con value mínimo entre dos puntos de venta")
    public ResponseEntity<MinDistanceResponse> getMinDistance(
            @RequestParam Long origen,
            @RequestParam Long destino) {
        MinDistanceResponse response = chargeService.calculateMinDistance(origen, destino);
        return ResponseEntity.ok(response);
    }
}
