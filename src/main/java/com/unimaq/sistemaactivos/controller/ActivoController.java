/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unimaq.sistemaactivos.controller;

import com.unimaq.sistemaactivos.model.Activo;
import com.unimaq.sistemaactivos.repository.ActivoRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/activos")
public class ActivoController {

    private final ActivoRepository activoRepository;

    public ActivoController(ActivoRepository activoRepository) {
        this.activoRepository = activoRepository;
    }

    @GetMapping
    public List<Activo> listarActivos() {
        return activoRepository.findAll();
    }
}
