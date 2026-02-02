package com.javasharks.puntosventaapi.service;

import com.javasharks.puntosventaapi.dto.AcreditacionRequest;
import com.javasharks.puntosventaapi.dto.AcreditacionResponse;
import com.javasharks.puntosventaapi.exception.ResourceNotFoundException;
import com.javasharks.puntosventaapi.model.Accreditation;
import com.javasharks.puntosventaapi.model.SellingPoint;
import com.javasharks.puntosventaapi.repository.AcreditacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccreditationService {

    private static final Logger log = LoggerFactory.getLogger(AccreditationService.class);
    private final AcreditacionRepository acreditacionRepository;
    private final SellingPointService sellingPointService;

    public AccreditationService(AcreditacionRepository acreditacionRepository, SellingPointService sellingPointService) {
        this.acreditacionRepository = acreditacionRepository;
        this.sellingPointService = sellingPointService;
    }

    /**
     * Procesa una nueva acreditación.
     * Enriquece la información con fecha actual y nombre del punto de venta.
     *
     * @param request Datos de la acreditación
     * @return Acreditación procesada y persistida
     */
    @Transactional
    public AcreditacionResponse procesarAcreditacion(AcreditacionRequest request) {
        log.info("Procesando acreditación para punto de venta ID: {}, importe: {}",
                request.puntoVentaId(), request.importe());

        SellingPoint sellingPoint = sellingPointService.findById(request.puntoVentaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Punto de venta con ID %d no encontrado", request.puntoVentaId())
                ));

        Accreditation accreditation = Accreditation.builder()
                .amount(request.importe())
                .sellingPointId(request.puntoVentaId())
                .sellingPointName(sellingPoint.nombre())
                .creationDate(LocalDateTime.now())
                .build();

        Accreditation saved = acreditacionRepository.save(accreditation);

        log.info("Acreditación procesada exitosamente. ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    /**
     * Recupera todas las acreditaciones.
     */
    @Transactional(readOnly = true)
    public List<AcreditacionResponse> getAllAcreditaciones() {
        log.debug("Recuperando todas las acreditaciones");

        return acreditacionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Recupera acreditaciones por punto de venta.
     */
    @Transactional(readOnly = true)
    public List<AcreditacionResponse> getAcreditacionesByPuntoVenta(Long puntoVentaId) {
        log.debug("Recuperando acreditaciones para punto de venta ID: {}", puntoVentaId);

        // Validar que el punto de venta existe
        if (!sellingPointService.exists(puntoVentaId)) {
            throw new ResourceNotFoundException(
                    String.format("Punto de venta con ID %d no encontrado", puntoVentaId)
            );
        }

        return acreditacionRepository.findByPuntoVentaId(puntoVentaId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AcreditacionResponse mapToResponse(Accreditation accreditation) {
        return new AcreditacionResponse(
                accreditation.getId(),
                accreditation.getAmount(),
                accreditation.getSellingPointId(),
                accreditation.getSellingPointName(),
                accreditation.getCreatedAt()
        );
    }
}
