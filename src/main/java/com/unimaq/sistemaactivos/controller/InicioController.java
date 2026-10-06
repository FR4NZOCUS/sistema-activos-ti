/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unimaq.sistemaactivos.controller;

import com.unimaq.sistemaactivos.model.Activo;

import com.unimaq.sistemaactivos.repository.ActivoRepository;
import com.unimaq.sistemaactivos.repository.AsignacionRepository;
import com.unimaq.sistemaactivos.repository.ColaboradorRepository;
import com.unimaq.sistemaactivos.repository.DevolucionRepository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    private final ActivoRepository activoRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final AsignacionRepository asignacionRepository;
    private final DevolucionRepository devolucionRepository;

    public InicioController(
            ActivoRepository activoRepository,
            ColaboradorRepository colaboradorRepository,
            AsignacionRepository asignacionRepository,
            DevolucionRepository devolucionRepository) {

        this.activoRepository = activoRepository;
        this.colaboradorRepository = colaboradorRepository;
        this.asignacionRepository = asignacionRepository;
        this.devolucionRepository = devolucionRepository;
    }


    /*
     * =====================================================
     * PANEL PRINCIPAL
     * =====================================================
     */
    @GetMapping("/")
    public String inicio(Model model) {


        /*
         * =================================================
         * TOTAL DE ACTIVOS
         * =================================================
         */
        long totalActivos
                = activoRepository.count();


        /*
         * =================================================
         * ACTIVOS DISPONIBLES
         * =================================================
         */
        long activosDisponibles
                = activoRepository
                        .countByEstadoNombreEstado(
                                "DISPONIBLE"
                        );


        /*
         * =================================================
         * ACTIVOS EN USO
         * =================================================
         *
         * Corresponde únicamente a los activos cuyo
         * estado actual es ASIGNADO_EN_USO.
         *
         * No incluye equipos pendientes de devolución.
         */
        long activosEnUso
                = activoRepository
                        .countByEstadoNombreEstado(
                                "ASIGNADO_EN_USO"
                        );


        /*
         * =================================================
         * ACTIVOS EN MANTENIMIENTO
         * =================================================
         */
        long activosMantenimiento
                = activoRepository
                        .countByEstadoNombreEstado(
                                "MANTENIMIENTO"
                        );


        /*
         * =================================================
         * ACTIVOS PENDIENTES DE DEVOLUCIÓN
         * =================================================
         *
         * Se consideran:
         *
         * - RENOVACION_POR_DEVOLVER
         * - CESADO_POR_DEVOLVER
         * - PRESTAMO_POR_GARANTIA
         *
         * Estos equipos todavía mantienen una asignación
         * activa, pero deben ser recibidos físicamente
         * por Soporte TI.
         */
        long activosRenovacionPorDevolver
                = activoRepository
                        .countByEstadoNombreEstado(
                                "RENOVACION_POR_DEVOLVER"
                        );

        long activosCesadoPorDevolver
                = activoRepository
                        .countByEstadoNombreEstado(
                                "CESADO_POR_DEVOLVER"
                        );

        long activosPrestamoPorGarantia
                = activoRepository
                        .countByEstadoNombreEstado(
                                "PRESTAMO_POR_GARANTIA"
                        );

        long activosPendientesDevolucion
                = activosRenovacionPorDevolver
                + activosCesadoPorDevolver
                + activosPrestamoPorGarantia;


        /*
         * =================================================
         * COLABORADORES ACTIVOS
         * =================================================
         */
        long colaboradoresActivos
                = colaboradorRepository
                        .countByEstadoColaborador(
                                "ACTIVO"
                        );


        /*
         * =================================================
         * ASIGNACIONES ACTIVAS
         * =================================================
         */
        long asignacionesActivas
                = asignacionRepository
                        .countByEstadoAsignacion(
                                "ACTIVA"
                        );


        /*
         * =================================================
         * DEVOLUCIONES PENDIENTES DE REVISIÓN
         * =================================================
         *
         * Estos equipos YA fueron recibidos físicamente
         * y se encuentran esperando revisión técnica.
         */
        long devolucionesPendientes
                = devolucionRepository
                        .countByCondicion(
                                "PENDIENTE_REVISION"
                        );


        /*
         * =================================================
         * GARANTÍAS PRÓXIMAS A VENCER
         * =================================================
         *
         * Se consideran garantías que vencen desde hoy
         * hasta los próximos 30 días.
         */
        LocalDate hoy
                = LocalDate.now();

        LocalDate limite
                = hoy.plusDays(30);

        List<Activo> proximos
                = activoRepository
                        .findByFechaVencimientoGarantiaBetweenOrderByFechaVencimientoGarantiaAsc(
                                hoy,
                                limite
                        );


        /*
         * No se contabilizan activos dados de baja.
         */
        long garantiasProximas
                = proximos.stream()
                        .filter(activo
                                -> activo.getEstado() == null
                                || !"BAJA".equals(
                                        activo
                                                .getEstado()
                                                .getNombreEstado()
                                )
                        )
                        .count();


        /*
         * =================================================
         * ENVIAR INDICADORES A LA VISTA
         * =================================================
         */
        model.addAttribute(
                "totalActivos",
                totalActivos
        );

        model.addAttribute(
                "activosDisponibles",
                activosDisponibles
        );


        /*
         * Conservamos también el nombre activosAsignados
         * para mantener compatibilidad con el HTML actual.
         */
        model.addAttribute(
                "activosAsignados",
                activosEnUso
        );


        /*
         * Nombre semánticamente más preciso para futuras
         * vistas.
         */
        model.addAttribute(
                "activosEnUso",
                activosEnUso
        );

        model.addAttribute(
                "activosMantenimiento",
                activosMantenimiento
        );

        model.addAttribute(
                "activosPendientesDevolucion",
                activosPendientesDevolucion
        );


        /*
         * Valores individuales.
         *
         * También pueden utilizarse más adelante para
         * indicadores o reportes específicos.
         */
        model.addAttribute(
                "activosRenovacionPorDevolver",
                activosRenovacionPorDevolver
        );

        model.addAttribute(
                "activosCesadoPorDevolver",
                activosCesadoPorDevolver
        );

        model.addAttribute(
                "activosPrestamoPorGarantia",
                activosPrestamoPorGarantia
        );

        model.addAttribute(
                "colaboradoresActivos",
                colaboradoresActivos
        );

        model.addAttribute(
                "asignacionesActivas",
                asignacionesActivas
        );

        model.addAttribute(
                "devolucionesPendientes",
                devolucionesPendientes
        );

        model.addAttribute(
                "garantiasProximas",
                garantiasProximas
        );

        return "inicio";
    }
}
