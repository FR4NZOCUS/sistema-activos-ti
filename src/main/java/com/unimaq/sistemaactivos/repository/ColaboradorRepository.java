/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.unimaq.sistemaactivos.repository;

import com.unimaq.sistemaactivos.model.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColaboradorRepository
        extends JpaRepository<Colaborador, Integer> {

    long countByEstadoColaborador(
            String estadoColaborador
    );
}
