package com.unimaq.sistemaactivos.controller;

import com.unimaq.sistemaactivos.model.Activo;
import com.unimaq.sistemaactivos.model.Asignacion;
import com.unimaq.sistemaactivos.model.Devolucion;
import com.unimaq.sistemaactivos.model.EstadoActivo;

import com.unimaq.sistemaactivos.repository.AsignacionRepository;
import com.unimaq.sistemaactivos.repository.DevolucionRepository;
import com.unimaq.sistemaactivos.repository.EstadoActivoRepository;

import com.unimaq.sistemaactivos.service.HistorialActivoService;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DevolucionController {

    private final DevolucionRepository devolucionRepository;
    private final AsignacionRepository asignacionRepository;
    private final EstadoActivoRepository estadoActivoRepository;
    private final HistorialActivoService historialActivoService;

    public DevolucionController(
            DevolucionRepository devolucionRepository,
            AsignacionRepository asignacionRepository,
            EstadoActivoRepository estadoActivoRepository,
            HistorialActivoService historialActivoService) {

        this.devolucionRepository = devolucionRepository;
        this.asignacionRepository = asignacionRepository;
        this.estadoActivoRepository = estadoActivoRepository;
        this.historialActivoService = historialActivoService;
    }


    /*
     * =====================================================
     * LISTADO DE DEVOLUCIONES
     * =====================================================
     */
    @GetMapping("/devoluciones")
    public String listarDevoluciones(
            Model model) {

        model.addAttribute(
                "devoluciones",
                devolucionRepository.findAll()
        );

        return "devoluciones";
    }


    /*
     * =====================================================
     * FORMULARIO DE NUEVA DEVOLUCIÓN
     * =====================================================
     */
    @GetMapping("/devoluciones/nueva/{idAsignacion}")
    public String nuevaDevolucion(
            @PathVariable Integer idAsignacion,
            Model model) {

        /*
         * Recuperar asignación.
         */
        Asignacion asignacion
                = asignacionRepository
                        .findById(idAsignacion)
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Asignación no encontrada."
                                )
                        );


        /*
         * =================================================
         * VALIDAR ASIGNACIÓN ACTIVA
         * =================================================
         */
        if (!"ACTIVA".equals(
                asignacion.getEstadoAsignacion())) {

            throw new IllegalArgumentException(
                    "La asignación seleccionada "
                    + "ya no se encuentra activa."
            );
        }


        /*
         * =================================================
         * VALIDAR ACTIVO
         * =================================================
         */
        if (asignacion.getActivo() == null) {

            throw new IllegalArgumentException(
                    "La asignación no tiene "
                    + "un activo asociado."
            );
        }


        /*
         * =================================================
         * VALIDAR ESTADO RETORNABLE
         * =================================================
         *
         * Se permiten devoluciones desde:
         *
         * - ASIGNADO_EN_USO
         * - RENOVACION_POR_DEVOLVER
         * - CESADO_POR_DEVOLVER
         * - PRESTAMO_POR_GARANTIA
         *
         * Esto permite trabajar tanto con las nuevas
         * asignaciones como con los estados provenientes
         * del inventario real migrado.
         */
        validarEstadoRetornable(
                asignacion.getActivo()
        );


        /*
         * Crear objeto de devolución.
         */
        Devolucion devolucion
                = new Devolucion();

        devolucion.setAsignacion(
                asignacion
        );

        model.addAttribute(
                "devolucion",
                devolucion
        );

        model.addAttribute(
                "asignacion",
                asignacion
        );

        return "nueva-devolucion";
    }


    /*
     * =====================================================
     * GUARDAR DEVOLUCIÓN
     * =====================================================
     */
    @Transactional
    @PostMapping("/devoluciones/guardar")
    public String guardarDevolucion(
            Devolucion devolucion) {


        /*
         * =================================================
         * VALIDAR ASIGNACIÓN RECIBIDA
         * =================================================
         */
        if (devolucion.getAsignacion() == null
                || devolucion
                        .getAsignacion()
                        .getIdAsignacion() == null) {

            throw new IllegalArgumentException(
                    "No se recibió una asignación válida."
            );
        }


        /*
         * =================================================
         * RECUPERAR ASIGNACIÓN REAL DESDE BD
         * =================================================
         *
         * No confiamos directamente en los datos enviados
         * desde el formulario.
         */
        Asignacion asignacion
                = asignacionRepository
                        .findById(
                                devolucion
                                        .getAsignacion()
                                        .getIdAsignacion()
                        )
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Asignación no encontrada."
                                )
                        );


        /*
         * =================================================
         * EVITAR DEVOLUCIONES DUPLICADAS
         * =================================================
         */
        if (!"ACTIVA".equals(
                asignacion.getEstadoAsignacion())) {

            throw new IllegalArgumentException(
                    "Esta asignación ya fue finalizada."
            );
        }


        /*
         * =================================================
         * RECUPERAR ACTIVO
         * =================================================
         */
        Activo activo
                = asignacion.getActivo();

        if (activo == null) {

            throw new IllegalArgumentException(
                    "La asignación no tiene "
                    + "un activo asociado."
            );
        }


        /*
         * =================================================
         * VALIDAR ESTADO DEL ACTIVO
         * =================================================
         */
        validarEstadoRetornable(
                activo
        );


        /*
         * =================================================
         * GUARDAR ESTADO ANTERIOR
         * =================================================
         *
         * Puede ser:
         *
         * ASIGNADO_EN_USO
         * RENOVACION_POR_DEVOLVER
         * CESADO_POR_DEVOLVER
         * PRESTAMO_POR_GARANTIA
         */
        EstadoActivo estadoAnterior
                = activo.getEstado();


        /*
         * =================================================
         * BUSCAR ESTADO
         * DEVUELTO_PENDIENTE_REVISION
         * =================================================
         */
        EstadoActivo estadoPendienteRevision
                = estadoActivoRepository
                        .findByNombreEstado(
                                "DEVUELTO_PENDIENTE_REVISION"
                        )
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "No existe el estado "
                                        + "DEVUELTO_PENDIENTE_REVISION."
                                )
                        );


        /*
         * =================================================
         * ACTUALIZAR ESTADO DEL ACTIVO
         * =================================================
         *
         * Una vez recibido físicamente el activo,
         * queda pendiente de revisión técnica.
         */
        activo.setEstado(
                estadoPendienteRevision
        );


        /*
         * =================================================
         * FINALIZAR ÚNICAMENTE ESTA ASIGNACIÓN
         * =================================================
         *
         * Un colaborador puede tener varias asignaciones
         * activas independientes.
         *
         * Ejemplo:
         *
         * Laptop
         * Monitor
         * Mouse
         *
         * La devolución de uno no debe finalizar los demás.
         */
        asignacion.setEstadoAsignacion(
                "FINALIZADA"
        );

        asignacionRepository.save(
                asignacion
        );


        /*
         * =================================================
         * GUARDAR DEVOLUCIÓN
         * =================================================
         */
        devolucion.setAsignacion(
                asignacion
        );

        devolucion.setCondicion(
                "PENDIENTE_REVISION"
        );

        devolucionRepository.save(
                devolucion
        );


        /*
         * =================================================
         * REGISTRAR HISTORIAL
         * =================================================
         */
        String nombreColaborador
                = asignacion.getColaborador() != null
                && asignacion
                        .getColaborador()
                        .getNombreCompleto() != null
                        ? asignacion
                                .getColaborador()
                                .getNombreCompleto()
                        : "colaborador no identificado";

        historialActivoService.registrarCambio(
                activo,
                estadoAnterior,
                estadoPendienteRevision,
                "Activo devuelto por "
                + nombreColaborador
                + ". Estado anterior: "
                + estadoAnterior.getNombreEstado()
                + ". Pendiente de revisión."
        );

        return "redirect:/devoluciones";
    }


    /*
     * =====================================================
     * FORMULARIO DE REVISIÓN DE DEVOLUCIÓN
     * =====================================================
     */
    @GetMapping("/devoluciones/revisar/{id}")
    public String revisarDevolucion(
            @PathVariable("id") Integer id,
            Model model) {


        /*
         * Recuperar devolución.
         */
        Devolucion devolucion
                = devolucionRepository
                        .findById(id)
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Devolución no encontrada."
                                )
                        );


        /*
         * =================================================
         * SOLO DEVOLUCIONES PENDIENTES
         * =================================================
         */
        if (!"PENDIENTE_REVISION".equals(
                devolucion.getCondicion())) {

            throw new IllegalArgumentException(
                    "La devolución ya fue revisada."
            );
        }

        model.addAttribute(
                "devolucion",
                devolucion
        );

        return "revisar-devolucion";
    }


    /*
     * =====================================================
     * GUARDAR RESULTADO DE REVISIÓN
     * =====================================================
     */
    @Transactional
    @PostMapping("/devoluciones/revisar")
    public String guardarRevision(
            @RequestParam Integer idDevolucion,
            @RequestParam String nuevoEstado,
            @RequestParam(
                    value = "observacionesRevision",
                    required = false
            ) String observacionesRevision) {


        /*
         * =================================================
         * RECUPERAR DEVOLUCIÓN
         * =================================================
         */
        Devolucion devolucion
                = devolucionRepository
                        .findById(idDevolucion)
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Devolución no encontrada."
                                )
                        );


        /*
         * =================================================
         * EVITAR DOBLE REVISIÓN
         * =================================================
         */
        if (!"PENDIENTE_REVISION".equals(
                devolucion.getCondicion())) {

            throw new IllegalArgumentException(
                    "La devolución ya fue revisada."
            );
        }


        /*
         * =================================================
         * VALIDAR ASIGNACIÓN Y ACTIVO
         * =================================================
         */
        Asignacion asignacion
                = devolucion.getAsignacion();

        if (asignacion == null
                || asignacion.getActivo() == null) {

            throw new IllegalArgumentException(
                    "La devolución no tiene "
                    + "un activo asociado."
            );
        }

        Activo activo
                = asignacion.getActivo();


        /*
         * =================================================
         * VALIDAR ESTADO PENDIENTE DE REVISIÓN
         * =================================================
         */
        if (activo.getEstado() == null
                || !"DEVUELTO_PENDIENTE_REVISION".equals(
                        activo
                                .getEstado()
                                .getNombreEstado()
                )) {

            throw new IllegalArgumentException(
                    "El activo no se encuentra "
                    + "pendiente de revisión."
            );
        }


        /*
         * =================================================
         * VALIDAR ESTADO FINAL
         * =================================================
         *
         * Luego de la revisión física el equipo puede:
         *
         * - volver a estar DISPONIBLE;
         * - pasar a MANTENIMIENTO;
         * - darse de BAJA.
         */
        if (!"DISPONIBLE".equals(nuevoEstado)
                && !"MANTENIMIENTO".equals(nuevoEstado)
                && !"BAJA".equals(nuevoEstado)) {

            throw new IllegalArgumentException(
                    "Estado de revisión no permitido."
            );
        }


        /*
         * Estado anterior.
         */
        EstadoActivo estadoAnterior
                = activo.getEstado();


        /*
         * Recuperar nuevo estado.
         */
        EstadoActivo estadoNuevo
                = estadoActivoRepository
                        .findByNombreEstado(
                                nuevoEstado
                        )
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "No existe el estado seleccionado."
                                )
                        );


        /*
         * =================================================
         * ACTUALIZAR ACTIVO
         * =================================================
         */
        activo.setEstado(
                estadoNuevo
        );


        /*
         * =================================================
         * ACTUALIZAR DEVOLUCIÓN
         * =================================================
         */
        devolucion.setCondicion(
                nuevoEstado
        );


        /*
         * =================================================
         * OBSERVACIONES DE REVISIÓN
         * =================================================
         *
         * Se conserva cualquier observación registrada
         * inicialmente durante la devolución.
         */
        if (observacionesRevision != null
                && !observacionesRevision.isBlank()) {

            String observacionAnterior
                    = devolucion.getObservaciones();

            if (observacionAnterior == null
                    || observacionAnterior.isBlank()) {

                devolucion.setObservaciones(
                        observacionesRevision
                );

            } else {

                devolucion.setObservaciones(
                        observacionAnterior
                        + " | Revisión: "
                        + observacionesRevision
                );
            }
        }

        devolucionRepository.save(
                devolucion
        );


        /*
         * =================================================
         * REGISTRAR HISTORIAL
         * =================================================
         */
        String comentario
                = "Revisión de devolución. "
                + "Resultado: "
                + nuevoEstado;

        if (observacionesRevision != null
                && !observacionesRevision.isBlank()) {

            comentario
                    = comentario
                    + ". "
                    + observacionesRevision;
        }

        historialActivoService.registrarCambio(
                activo,
                estadoAnterior,
                estadoNuevo,
                comentario
        );

        return "redirect:/devoluciones";
    }


    /*
     * =====================================================
     * VALIDAR ESTADO RETORNABLE
     * =====================================================
     *
     * Determina si un activo puede iniciar el proceso
     * físico de devolución.
     *
     * Estos estados indican que el activo todavía mantiene
     * una relación vigente con un colaborador y puede ser
     * recibido por Soporte TI.
     */
    private void validarEstadoRetornable(
            Activo activo) {

        if (activo == null
                || activo.getEstado() == null
                || activo
                        .getEstado()
                        .getNombreEstado() == null) {

            throw new IllegalArgumentException(
                    "El activo no tiene "
                    + "un estado válido."
            );
        }

        String nombreEstado
                = activo
                        .getEstado()
                        .getNombreEstado();

        boolean estadoPermitido
                = "ASIGNADO_EN_USO".equals(
                        nombreEstado
                )
                || "RENOVACION_POR_DEVOLVER".equals(
                        nombreEstado
                )
                || "CESADO_POR_DEVOLVER".equals(
                        nombreEstado
                )
                || "PRESTAMO_POR_GARANTIA".equals(
                        nombreEstado
                );

        if (!estadoPermitido) {

            throw new IllegalArgumentException(
                    "El activo no puede iniciar "
                    + "una devolución desde el estado: "
                    + nombreEstado
                    + "."
            );
        }
    }
}
