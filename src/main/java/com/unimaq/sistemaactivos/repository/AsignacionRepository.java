/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unimaq.sistemaactivos.repository;

import com.unimaq.sistemaactivos.model.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsignacionRepository
        extends JpaRepository<Asignacion, Integer> {

    long countByEstadoAsignacion(
            String estadoAsignacion
    );
}
