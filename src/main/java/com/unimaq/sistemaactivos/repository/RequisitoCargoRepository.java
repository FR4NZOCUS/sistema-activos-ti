/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unimaq.sistemaactivos.repository;

import com.unimaq.sistemaactivos.model.RequisitoCargo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RequisitoCargoRepository
        extends JpaRepository<RequisitoCargo, Integer> {

    List<RequisitoCargo>
            findByCargoIdCargo(
                    Integer idCargo
            );

    List<RequisitoCargo>
            findByCargoIdCargoAndTipoRequisito(
                    Integer idCargo,
                    String tipoRequisito
            );

    Optional<RequisitoCargo>
            findByCargoIdCargoAndTipoEquipoIdTipoEquipo(
                    Integer idCargo,
                    Integer idTipoEquipo
            );
}
