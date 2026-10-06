/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unimaq.sistemaactivos.controller;

import com.unimaq.sistemaactivos.model.Activo;
import com.unimaq.sistemaactivos.repository.ActivoRepository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AlertaGarantiaController {

    private final ActivoRepository activoRepository;

    public AlertaGarantiaController(
            ActivoRepository activoRepository) {

        this.activoRepository
                = activoRepository;
    }


    /*
     * =====================================================
     * ALERTAS DE GARANTÍA
     * =====================================================
     *
     * Clasificación:
     *
     * 1. Garantía vencida:
     *
     *    fechaVencimientoGarantia < hoy
     *
     * 2. Garantía próxima a vencer:
     *
     *    hoy <= fechaVencimientoGarantia <= hoy + 30 días
     *
     * Los activos con estado BAJA no se consideran.
     */
    @GetMapping("/alertas-garantia")
    public String mostrarAlertas(
            Model model) {


        /*
         * =================================================
         * FECHA ACTUAL
         * =================================================
         */
        LocalDate hoy
                = LocalDate.now();


        /*
         * Periodo de alerta:
         * próximos 30 días.
         */
        LocalDate limite
                = hoy.plusDays(30);


        /*
         * =================================================
         * GARANTÍAS PRÓXIMAS A VENCER
         * =================================================
         *
         * BETWEEN incluye ambas fechas:
         *
         * hoy
         * hasta
         * hoy + 30 días
         */
        List<Activo> activosProximos
                = activoRepository
                        .findByFechaVencimientoGarantiaBetweenOrderByFechaVencimientoGarantiaAsc(
                                hoy,
                                limite
                        );


        /*
         * =================================================
         * GARANTÍAS VENCIDAS
         * =================================================
         *
         * Se consideran vencidas únicamente aquellas
         * cuya fecha sea anterior al día actual.
         */
        List<Activo> activosVencidos
                = activoRepository
                        .findByFechaVencimientoGarantiaBeforeOrderByFechaVencimientoGarantiaAsc(
                                hoy
                        );


        /*
         * =================================================
         * EXCLUIR ACTIVOS EN BAJA
         * =================================================
         */
        activosProximos
                = activosProximos
                        .stream()
                        .filter(activo
                                -> activo.getEstado() == null
                                || activo
                                        .getEstado()
                                        .getNombreEstado() == null
                                || !"BAJA".equals(
                                        activo
                                                .getEstado()
                                                .getNombreEstado()
                                )
                        )
                        .toList();

        activosVencidos
                = activosVencidos
                        .stream()
                        .filter(activo
                                -> activo.getEstado() == null
                                || activo
                                        .getEstado()
                                        .getNombreEstado() == null
                                || !"BAJA".equals(
                                        activo
                                                .getEstado()
                                                .getNombreEstado()
                                )
                        )
                        .toList();


        /*
         * =================================================
         * ENVIAR INFORMACIÓN A LA VISTA
         * =================================================
         *
         * IMPORTANTE:
         *
         * Estos nombres deben coincidir exactamente con
         * alertas-garantia.html.
         */
        model.addAttribute(
                "activosProximos",
                activosProximos
        );

        model.addAttribute(
                "activosVencidos",
                activosVencidos
        );

        model.addAttribute(
                "hoy",
                hoy
        );

        model.addAttribute(
                "limite",
                limite
        );

        return "alertas-garantia";
    }
}
