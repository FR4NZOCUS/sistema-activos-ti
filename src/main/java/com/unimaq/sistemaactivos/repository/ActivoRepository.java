/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unimaq.sistemaactivos.repository;

import com.unimaq.sistemaactivos.model.Activo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivoRepository
        extends JpaRepository<Activo, Integer> {

    List<Activo> findByEstadoNombreEstado(
            String nombreEstado
    );

    long countByEstadoNombreEstado(
            String nombreEstado
    );

    List<Activo>
            findByFechaVencimientoGarantiaBetweenOrderByFechaVencimientoGarantiaAsc(
                    LocalDate fechaInicio,
                    LocalDate fechaFin
            );

    List<Activo>
            findByFechaVencimientoGarantiaBeforeOrderByFechaVencimientoGarantiaAsc(
                    LocalDate fecha
            );
}
