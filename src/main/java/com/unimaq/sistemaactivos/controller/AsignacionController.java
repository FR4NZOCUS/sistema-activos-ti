package com.unimaq.sistemaactivos.controller;

import com.unimaq.sistemaactivos.model.Activo;
import com.unimaq.sistemaactivos.model.Asignacion;
import com.unimaq.sistemaactivos.model.Colaborador;
import com.unimaq.sistemaactivos.model.EstadoActivo;

import com.unimaq.sistemaactivos.repository.ActivoRepository;
import com.unimaq.sistemaactivos.repository.AsignacionRepository;
import com.unimaq.sistemaactivos.repository.ColaboradorRepository;
import com.unimaq.sistemaactivos.repository.EstadoActivoRepository;

import com.unimaq.sistemaactivos.service.HistorialActivoService;
import com.unimaq.sistemaactivos.service.RecomendacionActivoService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AsignacionController {

    private final AsignacionRepository asignacionRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final ActivoRepository activoRepository;
    private final EstadoActivoRepository estadoActivoRepository;

    private final RecomendacionActivoService recomendacionActivoService;
    private final HistorialActivoService historialActivoService;

    public AsignacionController(
            AsignacionRepository asignacionRepository,
            ColaboradorRepository colaboradorRepository,
            ActivoRepository activoRepository,
            EstadoActivoRepository estadoActivoRepository,
            RecomendacionActivoService recomendacionActivoService,
            HistorialActivoService historialActivoService) {

        this.asignacionRepository = asignacionRepository;
        this.colaboradorRepository = colaboradorRepository;
        this.activoRepository = activoRepository;
        this.estadoActivoRepository = estadoActivoRepository;

        this.recomendacionActivoService
                = recomendacionActivoService;

        this.historialActivoService
                = historialActivoService;
    }


    /*
     * =====================================================
     * LISTADO DE ASIGNACIONES
     * =====================================================
     */
    @GetMapping("/asignaciones")
    public String listarAsignaciones(
            Model model) {

        model.addAttribute(
                "asignaciones",
                asignacionRepository.findAll()
        );

        return "asignaciones";
    }


    /*
     * =====================================================
     * NUEVA ASIGNACIÓN
     * =====================================================
     */
    @GetMapping("/asignaciones/nueva")
    public String nuevaAsignacion(
            @RequestParam(
                    value = "colaboradorId",
                    required = false
            ) Integer colaboradorId,
            Model model) {

        Asignacion asignacion
                = new Asignacion();

        model.addAttribute(
                "asignacion",
                asignacion
        );


        /*
         * =================================================
         * LISTADO DE COLABORADORES
         * =================================================
         */
        model.addAttribute(
                "colaboradores",
                colaboradorRepository.findAll()
        );


        /*
         * Si todavía no se seleccionó colaborador,
         * mostramos únicamente el primer paso.
         */
        if (colaboradorId == null) {

            return "nueva-asignacion";
        }


        /*
         * =================================================
         * BUSCAR COLABORADOR
         * =================================================
         */
        Colaborador colaborador
                = colaboradorRepository
                        .findById(colaboradorId)
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Colaborador no encontrado."
                                )
                        );


        /*
         * Asociar colaborador al formulario.
         */
        asignacion.setColaborador(
                colaborador
        );

        model.addAttribute(
                "colaboradorSeleccionado",
                colaborador
        );


        /*
         * =================================================
         * ACTIVO PRINCIPAL SELECCIONADO AUTOMÁTICAMENTE
         * =================================================
         *
         * Ya no pedimos al usuario que escoja una laptop.
         *
         * El sistema selecciona automáticamente la
         * opción más adecuada según:
         *
         * - cargo;
         * - tipo de equipo;
         * - procesador;
         * - generación;
         * - RAM;
         * - almacenamiento;
         * - disponibilidad.
         */
        Activo activoPrincipalAutomatico
                = recomendacionActivoService
                        .obtenerActivoPrincipalAutomatico(
                                colaboradorId
                        );

        model.addAttribute(
                "activoPrincipalAutomatico",
                activoPrincipalAutomatico
        );


        /*
         * Por compatibilidad temporal mantenemos también
         * la lista completa de recomendados.
         *
         * Después podremos eliminarla cuando terminemos
         * de adaptar la plantilla.
         */
        model.addAttribute(
                "activosRecomendados",
                recomendacionActivoService
                        .obtenerRecomendados(
                                colaboradorId
                        )
        );


        /*
         * =================================================
         * REQUISITOS COMPLEMENTARIOS
         * =================================================
         */
        model.addAttribute(
                "requisitosComplementarios",
                recomendacionActivoService
                        .obtenerComplementarios(
                                colaboradorId
                        )
        );


        /*
         * =================================================
         * ACTIVOS COMPLEMENTARIOS DISPONIBLES
         * =================================================
         */
        model.addAttribute(
                "activosComplementarios",
                recomendacionActivoService
                        .obtenerActivosComplementarios(
                                colaboradorId
                        )
        );

        return "nueva-asignacion";
    }


    /*
     * =====================================================
     * GUARDAR ASIGNACIÓN
     * =====================================================
     *
     * El activo principal YA NO se toma desde
     * formulario.getActivo().
     *
     * El servidor lo calcula nuevamente al momento
     * de confirmar la asignación.
     *
     * Esto evita:
     *
     * - modificaciones manuales desde el navegador;
     * - asignaciones que no correspondan al cargo;
     * - seleccionar un equipo que dejó de ser adecuado;
     * - depender de un radio button enviado por HTML.
     */
    @Transactional
    @PostMapping("/asignaciones/guardar")
    public String guardarAsignacion(
            Asignacion formulario,
            @RequestParam(
                    value = "activosComplementariosSeleccionados",
                    required = false
            ) List<Integer> idsComplementarios) {


        /*
         * =================================================
         * VALIDAR COLABORADOR
         * =================================================
         */
        if (formulario.getColaborador() == null
                || formulario
                        .getColaborador()
                        .getIdColaborador() == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un colaborador."
            );
        }

        Integer idColaborador
                = formulario
                        .getColaborador()
                        .getIdColaborador();


        /*
         * =================================================
         * RECUPERAR COLABORADOR REAL
         * =================================================
         */
        Colaborador colaborador
                = colaboradorRepository
                        .findById(idColaborador)
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Colaborador no encontrado."
                                )
                        );


        /*
         * =================================================
         * SELECCIONAR ACTIVO PRINCIPAL AUTOMÁTICAMENTE
         * =================================================
         *
         * MUY IMPORTANTE:
         *
         * El activo se vuelve a calcular aquí.
         *
         * No utilizamos un ID enviado por el navegador.
         */
        Activo activoSeleccionado
                = recomendacionActivoService
                        .obtenerActivoPrincipalAutomatico(
                                idColaborador
                        );


        /*
         * Si no existe ningún equipo que cumpla los
         * requisitos, no se realiza la asignación.
         */
        if (activoSeleccionado == null) {

            throw new IllegalArgumentException(
                    "No existe actualmente un activo "
                    + "principal disponible que cumpla "
                    + "los requisitos configurados "
                    + "para el cargo del colaborador."
            );
        }


        /*
         * Recuperamos nuevamente el activo desde
         * el repositorio.
         *
         * Así trabajamos con el registro gestionado
         * por JPA dentro de la transacción.
         */
        Activo activoPrincipal
                = activoRepository
                        .findById(
                                activoSeleccionado
                                        .getIdActivo()
                        )
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "El activo principal "
                                        + "seleccionado automáticamente "
                                        + "ya no existe."
                                )
                        );


        /*
         * =================================================
         * VALIDAR QUE SIGA DISPONIBLE
         * =================================================
         */
        validarActivoDisponible(
                activoPrincipal
        );


        /*
         * =================================================
         * ESTADO ASIGNADO_EN_USO
         * =================================================
         */
        EstadoActivo estadoAsignado
                = estadoActivoRepository
                        .findByNombreEstado(
                                "ASIGNADO_EN_USO"
                        )
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "No existe el estado "
                                        + "ASIGNADO_EN_USO."
                                )
                        );


        /*
         * =================================================
         * REGISTRAR ACTIVO PRINCIPAL
         * =================================================
         */
        registrarAsignacionIndividual(
                colaborador,
                activoPrincipal,
                estadoAsignado,
                formulario.getObservaciones(),
                "Activo principal asignado "
                + "automáticamente según los "
                + "requisitos del cargo a "
                + colaborador.getNombreCompleto()
        );


        /*
         * =================================================
         * ACTIVOS COMPLEMENTARIOS
         * =================================================
         *
         * Los complementarios continúan siendo
         * seleccionados manualmente por Soporte TI.
         *
         * Esto es intencional porque elementos como
         * monitor, mouse o celular pueden depender
         * de disponibilidad o necesidad operativa.
         */
        if (idsComplementarios != null
                && !idsComplementarios.isEmpty()) {


            /*
             * Obtener los complementarios permitidos
             * para el cargo.
             */
            List<Activo> complementariosPermitidos
                    = recomendacionActivoService
                            .obtenerActivosComplementarios(
                                    idColaborador
                            );


            /*
             * Crear conjunto de IDs válidos.
             */
            Set<Integer> idsPermitidos
                    = new HashSet<>();

            for (Activo activo
                    : complementariosPermitidos) {

                idsPermitidos.add(
                        activo.getIdActivo()
                );
            }


            /*
             * Evitar procesar IDs duplicados.
             */
            Set<Integer> idsProcesados
                    = new HashSet<>();

            for (Integer idComplementario
                    : idsComplementarios) {

                if (idComplementario == null) {

                    continue;
                }


                /*
                 * Evitar duplicados.
                 */
                if (idsProcesados.contains(
                        idComplementario)) {

                    continue;
                }

                idsProcesados.add(
                        idComplementario
                );


                /*
                 * Validar que realmente sea un
                 * complemento permitido.
                 */
                if (!idsPermitidos.contains(
                        idComplementario)) {

                    throw new IllegalArgumentException(
                            "Uno de los activos "
                            + "complementarios seleccionados "
                            + "no corresponde a los "
                            + "requisitos del cargo."
                    );
                }


                /*
                 * Recuperar activo.
                 */
                Activo activoComplementario
                        = activoRepository
                                .findById(
                                        idComplementario
                                )
                                .orElseThrow(()
                                        -> new IllegalArgumentException(
                                                "Activo complementario "
                                                + "no encontrado."
                                        )
                                );


                /*
                 * Validar que continúe disponible.
                 */
                validarActivoDisponible(
                        activoComplementario
                );


                /*
                 * Evitar que por alguna configuración
                 * incorrecta el mismo activo principal
                 * sea procesado también como complemento.
                 */
                if (activoComplementario
                        .getIdActivo()
                        .equals(
                                activoPrincipal
                                        .getIdActivo()
                        )) {

                    throw new IllegalArgumentException(
                            "El activo principal no puede "
                            + "registrarse también como "
                            + "activo complementario."
                    );
                }


                /*
                 * Registrar asignación independiente.
                 */
                registrarAsignacionIndividual(
                        colaborador,
                        activoComplementario,
                        estadoAsignado,
                        formulario.getObservaciones(),
                        "Activo complementario asignado a "
                        + colaborador.getNombreCompleto()
                );
            }
        }

        return "redirect:/asignaciones";
    }


    /*
     * =====================================================
     * REGISTRAR ASIGNACIÓN INDIVIDUAL
     * =====================================================
     *
     * Cada activo genera su propio registro en
     * la tabla asignaciones.
     *
     * Esto permite devolver posteriormente:
     *
     * - laptop;
     * - monitor;
     * - mouse;
     * - celular;
     *
     * de manera independiente.
     */
    private void registrarAsignacionIndividual(
            Colaborador colaborador,
            Activo activo,
            EstadoActivo estadoAsignado,
            String observaciones,
            String comentarioHistorial) {


        /*
         * Guardar estado anterior.
         */
        EstadoActivo estadoAnterior
                = activo.getEstado();


        /*
         * Cambiar estado del activo.
         */
        activo.setEstado(
                estadoAsignado
        );

        activoRepository.save(
                activo
        );


        /*
         * Crear asignación independiente.
         */
        Asignacion nuevaAsignacion
                = new Asignacion();

        nuevaAsignacion.setColaborador(
                colaborador
        );

        nuevaAsignacion.setActivo(
                activo
        );

        nuevaAsignacion.setEstadoAsignacion(
                "ACTIVA"
        );

        nuevaAsignacion.setObservaciones(
                observaciones
        );


        /*
         * Guardar asignación.
         */
        asignacionRepository.save(
                nuevaAsignacion
        );


        /*
         * Registrar trazabilidad.
         */
        historialActivoService.registrarCambio(
                activo,
                estadoAnterior,
                estadoAsignado,
                comentarioHistorial
        );
    }


    /*
     * =====================================================
     * VALIDAR DISPONIBILIDAD
     * =====================================================
     */
    private void validarActivoDisponible(
            Activo activo) {

        if (activo.getEstado() == null
                || activo
                        .getEstado()
                        .getNombreEstado() == null
                || !"DISPONIBLE".equals(
                        activo
                                .getEstado()
                                .getNombreEstado()
                )) {

            throw new IllegalArgumentException(
                    "El activo "
                    + activo.getNumeroInventario()
                    + " ya no se encuentra disponible."
            );
        }
    }


    /*
     * =====================================================
     * VER DETALLE
     * =====================================================
     */
    @GetMapping("/asignaciones/ver/{id}")
    public String verAsignacion(
            @PathVariable("id") Integer id,
            Model model) {

        Asignacion asignacion
                = asignacionRepository
                        .findById(id)
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Asignación no encontrada "
                                        + "con ID: "
                                        + id
                                )
                        );

        model.addAttribute(
                "asignacion",
                asignacion
        );

        return "ver-asignacion";
    }
}
