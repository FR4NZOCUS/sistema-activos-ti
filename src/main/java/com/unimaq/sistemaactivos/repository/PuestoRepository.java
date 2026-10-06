package com.unimaq.sistemaactivos.repository;

import com.unimaq.sistemaactivos.model.Puesto;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PuestoRepository
        extends JpaRepository<Puesto, Integer> {

    Optional<Puesto> findByNombrePuesto(
            String nombrePuesto);

    boolean existsByNombrePuesto(
            String nombrePuesto);
}
