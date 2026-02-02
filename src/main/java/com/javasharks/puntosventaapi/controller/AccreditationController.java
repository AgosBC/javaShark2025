package com.javasharks.puntosventaapi.controller;

import com.javasharks.puntosventaapi.dto.AcreditacionRequest;
import com.javasharks.puntosventaapi.dto.AcreditacionResponse;
import com.javasharks.puntosventaapi.service.AccreditationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/acreditaciones")
@Tag(name = "Acreditaciones", description = "API para gestión de acreditaciones")
public class AccreditationController {

    private final AccreditationService accreditationService;

    public AccreditationController(AccreditationService accreditationService) {
        this.accreditationService = accreditationService;
    }

    @PostMapping
    @Operation(summary = "Procesar una nueva acreditación")
    public ResponseEntity<AcreditacionResponse> addAcreditacion(
            @Valid @RequestBody AcreditacionRequest request) {
        AcreditacionResponse response = accreditationService.procesarAcreditacion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    @Operation(summary = "Obtener todas las acreditaciones")
    public ResponseEntity<List<AcreditacionResponse>> getAllAcreditaciones() {
        List<AcreditacionResponse> acreditaciones = accreditationService.getAllAcreditaciones();
        return ResponseEntity.ok(acreditaciones);
    }
    
    @GetMapping("/punto-venta/{puntoVentaId}")
    @Operation(summary = "Obtener acreditaciones por punto de venta")
    public ResponseEntity<List<AcreditacionResponse>> getAcreditacionesByPuntoVenta(
            @PathVariable Long puntoVentaId) {
        List<AcreditacionResponse> acreditaciones =
            accreditationService.getAcreditacionesByPuntoVenta(puntoVentaId);
        return ResponseEntity.ok(acreditaciones);
    }
}
