/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unimaq.sistemaactivos.repository;

import com.unimaq.sistemaactivos.model.HistorialActivo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialActivoRepository
        extends JpaRepository<HistorialActivo, Integer> {

    List<HistorialActivo>
            findByActivoIdActivoOrderByFechaCambioDesc(
                    Integer idActivo
            );
}
