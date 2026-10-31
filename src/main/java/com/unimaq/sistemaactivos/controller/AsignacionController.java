package com.unimaq.sistemaactivos.controller;

import com.unimaq.sistemaactivos.model.Activo;
import com.unimaq.sistemaactivos.model.Asignacion;
import com.unimaq.sistemaactivos.model.Colaborador;
import com.unimaq.sistemaactivos.model.EstadoActivo;

import com.unimaq.sistemaactivos.repository.ActivoRepository;
import com.unimaq.sistemaactivos.repository.AsignacionRepository;
import com.unimaq.sistemaactivos.repository.ColaboradorRepository;
import com.unimaq.sistemaactivos.repository.EstadoActivoRepository;
import com.unimaq.sistemaactivos.repository.SedeRepository;
import com.unimaq.sistemaactivos.repository.TipoEquipoRepository;

import com.unimaq.sistemaactivos.service.HistorialActivoService;
import com.unimaq.sistemaactivos.service.RecomendacionActivoService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

    private final TipoEquipoRepository tipoEquipoRepository;

    private final SedeRepository sedeRepository;

    private final RecomendacionActivoService recomendacionActivoService;

    private final HistorialActivoService historialActivoService;


    /*
     * =====================================================
     * CONSTRUCTOR
     * =====================================================
     */
    public AsignacionController(
            AsignacionRepository asignacionRepository,
            ColaboradorRepository colaboradorRepository,
            ActivoRepository activoRepository,
            EstadoActivoRepository estadoActivoRepository,
            TipoEquipoRepository tipoEquipoRepository,
            SedeRepository sedeRepository,
            RecomendacionActivoService recomendacionActivoService,
            HistorialActivoService historialActivoService) {

        this.asignacionRepository
                = asignacionRepository;

        this.colaboradorRepository
                = colaboradorRepository;

        this.activoRepository
                = activoRepository;

        this.estadoActivoRepository
                = estadoActivoRepository;

        this.tipoEquipoRepository
                = tipoEquipoRepository;

        this.sedeRepository
                = sedeRepository;

        this.recomendacionActivoService
                = recomendacionActivoService;

        this.historialActivoService
                = historialActivoService;
    }


    /*
     * =====================================================
     * LISTADO CON FILTROS Y PAGINACIÓN
     * =====================================================
     */
    @GetMapping("/asignaciones")
    public String listarAsignaciones(
            @RequestParam(
                    value = "q",
                    required = false
            ) String q,
            @RequestParam(
                    value = "estado",
                    required = false
            ) String estado,
            @RequestParam(
                    value = "tipoId",
                    required = false
            ) Integer tipoId,
            @RequestParam(
                    value = "sedeId",
                    required = false
            ) Integer sedeId,
            @RequestParam(
                    value = "page",
                    defaultValue = "0"
            ) int page,
            @RequestParam(
                    value = "size",
                    defaultValue = "10"
            ) int size,
            Model model) {


        /*
         * =================================================
         * VALIDAR TAMAÑO
         * =================================================
         */
        if (size != 10
                && size != 20
                && size != 50) {

            size = 10;
        }


        /*
         * Evitar páginas negativas.
         */
        if (page < 0) {

            page = 0;
        }


        /*
         * Limpiar búsqueda.
         */
        if (q != null) {

            q = q.trim();

            if (q.isEmpty()) {

                q = null;
            }
        }


        /*
         * Limpiar estado.
         */
        if (estado != null) {

            estado = estado.trim();

            if (estado.isEmpty()) {

                estado = null;
            }
        }


        /*
         * =================================================
         * PAGINACIÓN
         * =================================================
         */
        Pageable pageable
                = PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.DESC,
                                "idAsignacion"
                        )
                );


        /*
         * =================================================
         * CONSULTA CON FILTROS
         * =================================================
         */
        Page<Asignacion> paginaAsignaciones
                = asignacionRepository
                        .buscarConFiltros(
                                q,
                                estado,
                                tipoId,
                                sedeId,
                                pageable
                        );


        /*
         * Si se solicita una página que ya no existe
         * después de aplicar filtros, volver a página 0.
         */
        if (paginaAsignaciones.getTotalPages() > 0
                && page >= paginaAsignaciones.getTotalPages()) {

            pageable
                    = PageRequest.of(
                            0,
                            size,
                            Sort.by(
                                    Sort.Direction.DESC,
                                    "idAsignacion"
                            )
                    );

            paginaAsignaciones
                    = asignacionRepository
                            .buscarConFiltros(
                                    q,
                                    estado,
                                    tipoId,
                                    sedeId,
                                    pageable
                            );
        }


        /*
         * =================================================
         * RESULTADOS
         * =================================================
         */
        model.addAttribute(
                "asignaciones",
                paginaAsignaciones.getContent()
        );

        model.addAttribute(
                "paginaAsignaciones",
                paginaAsignaciones
        );


        /*
         * =================================================
         * CATÁLOGOS PARA FILTROS
         * =================================================
         */
        model.addAttribute(
                "tiposEquipo",
                tipoEquipoRepository.findAll()
        );

        model.addAttribute(
                "sedes",
                sedeRepository.findAll()
        );


        /*
         * =================================================
         * CONSERVAR FILTROS
         * =================================================
         */
        model.addAttribute(
                "q",
                q
        );

        model.addAttribute(
                "estado",
                estado
        );

        model.addAttribute(
                "tipoId",
                tipoId
        );

        model.addAttribute(
                "sedeId",
                sedeId
        );

        model.addAttribute(
                "size",
                size
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
         * Lista de colaboradores.
         */
        model.addAttribute(
                "colaboradores",
                colaboradorRepository.findAll()
        );


        /*
         * Todavía no existe colaborador seleccionado.
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
                        .findById(
                                colaboradorId
                        )
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Colaborador no encontrado."
                                )
                        );


        /*
         * Asociar colaborador.
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
         * EQUIPO AUTOMÁTICO
         * =================================================
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
         * =================================================
         * DIAGNÓSTICO
         * =================================================
         */
        String mensajeRecomendacion
                = recomendacionActivoService
                        .obtenerDiagnostico(
                                colaboradorId
                        );

        model.addAttribute(
                "mensajeRecomendacion",
                mensajeRecomendacion
        );


        /*
         * Lista completa de recomendados.
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
         * COMPLEMENTARIOS CONFIGURADOS
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
         * ACTIVOS COMPLEMENTARIOS
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
                        .getIdColaborador()
                == null) {

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
         * RECUPERAR COLABORADOR
         * =================================================
         */
        Colaborador colaborador
                = colaboradorRepository
                        .findById(
                                idColaborador
                        )
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Colaborador no encontrado."
                                )
                        );


        /*
         * =================================================
         * CALCULAR NUEVAMENTE EL EQUIPO
         * =================================================
         */
        Activo activoSeleccionado
                = recomendacionActivoService
                        .obtenerActivoPrincipalAutomatico(
                                idColaborador
                        );


        /*
         * No existe equipo compatible.
         */
        if (activoSeleccionado == null) {

            String diagnostico
                    = recomendacionActivoService
                            .obtenerDiagnostico(
                                    idColaborador
                            );

            throw new IllegalArgumentException(
                    diagnostico != null
                            ? diagnostico
                            : "No existe actualmente un "
                            + "activo principal compatible."
            );
        }


        /*
         * Recuperar activo real.
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
         * Validar disponibilidad.
         */
        validarActivoDisponible(
                activoPrincipal
        );


        /*
         * =================================================
         * ESTADO ASIGNADO
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
         * ASIGNAR EQUIPO PRINCIPAL
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
         * COMPLEMENTARIOS
         * =================================================
         */
        if (idsComplementarios != null
                && !idsComplementarios.isEmpty()) {

            List<Activo> complementariosPermitidos
                    = recomendacionActivoService
                            .obtenerActivosComplementarios(
                                    idColaborador
                            );

            Set<Integer> idsPermitidos
                    = new HashSet<>();

            for (Activo activo
                    : complementariosPermitidos) {

                idsPermitidos.add(
                        activo.getIdActivo()
                );
            }

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
                if (!idsProcesados.add(
                        idComplementario)) {

                    continue;
                }


                /*
                 * Validar complemento permitido.
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
                 * Validar disponibilidad.
                 */
                validarActivoDisponible(
                        activoComplementario
                );


                /*
                 * No permitir principal
                 * como complementario.
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
                 * Registrar complemento.
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
     * REGISTRAR ASIGNACIÓN
     * =====================================================
     */
    private void registrarAsignacionIndividual(
            Colaborador colaborador,
            Activo activo,
            EstadoActivo estadoAsignado,
            String observaciones,
            String comentarioHistorial) {

        EstadoActivo estadoAnterior
                = activo.getEstado();


        /*
         * Cambiar estado.
         */
        activo.setEstado(
                estadoAsignado
        );

        activoRepository.save(
                activo
        );


        /*
         * Crear asignación.
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

        asignacionRepository.save(
                nuevaAsignacion
        );


        /*
         * Historial.
         */
        historialActivoService
                .registrarCambio(
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
                        .getNombreEstado()
                == null
                || !"DISPONIBLE"
                        .equals(
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
     * VER ASIGNACIÓN
     * =====================================================
     */
    @GetMapping("/asignaciones/ver/{id}")
    public String verAsignacion(
            @PathVariable("id") Integer id,
            Model model) {

        Asignacion asignacion
                = asignacionRepository
                        .findById(
                                id
                        )
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
