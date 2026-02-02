package com.javasharks.puntosventaapi.repository;

import com.javasharks.puntosventaapi.model.Accreditation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcreditacionRepository extends JpaRepository<Accreditation, Long> {

    List<Accreditation> findByPuntoVentaId(Long puntoVentaId);
}