/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unimaq.sistemaactivos.controller;

import com.unimaq.sistemaactivos.repository.RequisitoCargoRepository;
import com.unimaq.sistemaactivos.model.RequisitoCargo;
import com.unimaq.sistemaactivos.repository.CargoRepository;
import com.unimaq.sistemaactivos.repository.TipoEquipoRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RequisitosCargoController {

    private final RequisitoCargoRepository requisitoCargoRepository;
    private final CargoRepository cargoRepository;
    private final TipoEquipoRepository tipoEquipoRepository;

    public RequisitosCargoController(
            RequisitoCargoRepository requisitoCargoRepository,
            CargoRepository cargoRepository,
            TipoEquipoRepository tipoEquipoRepository) {

        this.requisitoCargoRepository = requisitoCargoRepository;
        this.cargoRepository = cargoRepository;
        this.tipoEquipoRepository = tipoEquipoRepository;
    }

    @GetMapping("/requisitos-cargo")
    public String listarRequisitos(Model model) {

        model.addAttribute(
                "requisitos",
                requisitoCargoRepository.findAll()
        );

        return "requisitos-cargo";
    }

    @GetMapping("/requisitos-cargo/editar/{id}")
    public String editarRequisito(
            @PathVariable("id") Integer id,
            Model model) {

        RequisitoCargo requisito
                = requisitoCargoRepository
                        .findById(id)
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Requisito no encontrado con ID: " + id
                                )
                        );

        model.addAttribute(
                "requisito",
                requisito
        );

        model.addAttribute(
                "cargos",
                cargoRepository.findAll()
        );

        model.addAttribute(
                "tiposEquipo",
                tipoEquipoRepository.findAll()
        );

        return "editar-requisito-cargo";
    }

    @PostMapping("/requisitos-cargo/guardar")
    public String guardarRequisito(
            RequisitoCargo requisito,
            RedirectAttributes redirectAttributes) {

        if (requisito.getCargo() == null
                || requisito.getCargo().getIdCargo() == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Debe seleccionar un cargo."
            );

            return "redirect:/requisitos-cargo";
        }

        if (requisito.getTipoEquipo() == null
                || requisito.getTipoEquipo().getIdTipoEquipo() == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Debe seleccionar un tipo de equipo."
            );

            return "redirect:/requisitos-cargo";
        }

        Integer idCargo
                = requisito.getCargo().getIdCargo();

        Integer idTipoEquipo
                = requisito.getTipoEquipo().getIdTipoEquipo();

        var existente
                = requisitoCargoRepository
                        .findByCargoIdCargoAndTipoEquipoIdTipoEquipo(
                                idCargo,
                                idTipoEquipo
                        );


        /*
     * Si estamos creando uno nuevo y ya existe
     * esa combinación, no permitimos duplicado.
         */
        if (requisito.getIdRequisito() == null
                && existente.isPresent()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Ya existe un requisito configurado para ese cargo y tipo de equipo."
            );

            return "redirect:/requisitos-cargo";
        }


        /*
     * Si estamos editando, permitimos conservar
     * su propia combinación.
         */
        if (requisito.getIdRequisito() != null
                && existente.isPresent()
                && !existente.get()
                        .getIdRequisito()
                        .equals(
                                requisito.getIdRequisito()
                        )) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Ya existe otro requisito con ese cargo y tipo de equipo."
            );

            return "redirect:/requisitos-cargo";
        }


        /*
     * Valores por defecto
         */
        if (requisito.getPrioridad() == null) {
            requisito.setPrioridad(1);
        }

        if (requisito.getRequiereMonitor() == null) {
            requisito.setRequiereMonitor(false);
        }

        if (requisito.getRequiereMovil() == null) {
            requisito.setRequiereMovil(false);
        }

        if (requisito.getTipoRequisito() == null
                || requisito.getTipoRequisito().isBlank()) {

            requisito.setTipoRequisito("PRINCIPAL");
        }

        boolean esNuevo
                = requisito.getIdRequisito() == null;

        requisitoCargoRepository.save(requisito);

        redirectAttributes.addFlashAttribute(
                "mensaje",
                requisito.getIdRequisito() == null
                ? "Requisito registrado correctamente."
                : "Requisito actualizado correctamente."
        );

        return "redirect:/requisitos-cargo";
    }

    @GetMapping("/requisitos-cargo/nuevo")
    public String nuevoRequisito(Model model) {

        model.addAttribute(
                "requisito",
                new RequisitoCargo()
        );

        model.addAttribute(
                "cargos",
                cargoRepository.findAll()
        );

        model.addAttribute(
                "tiposEquipo",
                tipoEquipoRepository.findAll()
        );

        return "nuevo-requisito-cargo";
    }
}
