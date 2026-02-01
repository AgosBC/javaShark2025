package com.javasharks.puntosventaapi.controller;

import com.javasharks.puntosventaapi.model.SellingPoint;
import com.javasharks.puntosventaapi.service.SellingPointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/puntos-venta")
@Tag(name = "Puntos de Venta", description = "API para gestión de puntos de venta")
public class SellingPointController {

    private final SellingPointService sellingPointService;

    public SellingPointController(SellingPointService sellingPointService) {
        this.sellingPointService = sellingPointService;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los puntos de venta")
    public ResponseEntity<List<SellingPoint>> getAll() {
        return ResponseEntity.ok(sellingPointService.findAll());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un punto de venta por ID")
    //TODO cambiar a un DTO
    public ResponseEntity<SellingPoint> getById(@PathVariable Long id) {
        return sellingPointService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(summary = "Crear un nuevo punto de venta")
    public ResponseEntity<SellingPoint> create(@Valid @RequestBody SellingPoint sellingPoint) {
        SellingPoint created = sellingPointService.create(sellingPoint);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un punto de venta existente")
    public ResponseEntity<SellingPoint> update(
            @PathVariable Long id,
            @Valid @RequestBody SellingPoint sellingPoint) {
        SellingPoint updated = sellingPointService.update(id, sellingPoint);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un punto de venta")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sellingPointService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
