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

    public AsignacionController(
            AsignacionRepository asignacionRepository,
            ColaboradorRepository colaboradorRepository,
            ActivoRepository activoRepository,
            EstadoActivoRepository estadoActivoRepository,
            TipoEquipoRepository tipoEquipoRepository,
            SedeRepository sedeRepository,
            RecomendacionActivoService recomendacionActivoService,
            HistorialActivoService historialActivoService) {

        this.asignacionRepository = asignacionRepository;
        this.colaboradorRepository = colaboradorRepository;
        this.activoRepository = activoRepository;
        this.estadoActivoRepository = estadoActivoRepository;
        this.tipoEquipoRepository = tipoEquipoRepository;
        this.sedeRepository = sedeRepository;
        this.recomendacionActivoService = recomendacionActivoService;
        this.historialActivoService = historialActivoService;
    }


    /*
     * =====================================================
     * LISTADO DE ASIGNACIONES CON FILTROS Y PAGINACIÓN
     * =====================================================
     */
    @GetMapping("/asignaciones")
    public String listarAsignaciones(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "estado", required = false) String estado,
            @RequestParam(value = "tipoId", required = false) Integer tipoId,
            @RequestParam(value = "sedeId", required = false) Integer sedeId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {

        if (page < 0) {
            page = 0;
        }

        if (size != 10 && size != 20 && size != 50) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "idAsignacion")
        );

        Page<Asignacion> paginaAsignaciones
                = asignacionRepository.buscarConFiltros(
                        q,
                        estado,
                        tipoId,
                        sedeId,
                        pageable
                );

        if (paginaAsignaciones.getTotalPages() > 0
                && page >= paginaAsignaciones.getTotalPages()) {

            page = 0;
            pageable = PageRequest.of(
                    0,
                    size,
                    Sort.by(Sort.Direction.DESC, "idAsignacion")
            );

            paginaAsignaciones
                    = asignacionRepository.buscarConFiltros(
                            q,
                            estado,
                            tipoId,
                            sedeId,
                            pageable
                    );
        }

        model.addAttribute(
                "asignaciones",
                paginaAsignaciones.getContent()
        );

        model.addAttribute(
                "paginaAsignaciones",
                paginaAsignaciones
        );

        model.addAttribute(
                "tiposEquipo",
                tipoEquipoRepository.findAll()
        );

        model.addAttribute(
                "sedes",
                sedeRepository.findAll()
        );

        model.addAttribute("q", q);
        model.addAttribute("estado", estado);
        model.addAttribute("tipoId", tipoId);
        model.addAttribute("sedeId", sedeId);
        model.addAttribute("size", size);

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

        Asignacion asignacion = new Asignacion();

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
         * Si todavía no se seleccionó colaborador,
         * solo mostramos el primer paso.
         */
        if (colaboradorId == null) {

            return "nueva-asignacion";
        }

        /*
         * Buscar colaborador seleccionado.
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
         * ACTIVOS PRINCIPALES RECOMENDADOS
         * =================================================
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
         * ACTIVOS COMPLEMENTARIOS REALES DISPONIBLES
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
     * Guarda:
     *
     * 1. Activo principal obligatorio.
     * 2. Cero o varios activos complementarios.
     *
     * Cada activo genera su propio registro
     * en la tabla asignaciones.
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

        /*
         * =================================================
         * VALIDAR ACTIVO PRINCIPAL
         * =================================================
         */
        if (formulario.getActivo() == null
                || formulario
                        .getActivo()
                        .getIdActivo() == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un activo principal."
            );
        }

        Integer idColaborador
                = formulario
                        .getColaborador()
                        .getIdColaborador();

        Integer idActivoPrincipal
                = formulario
                        .getActivo()
                        .getIdActivo();

        /*
         * Recuperar colaborador real.
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
         * VALIDAR QUE EL PRINCIPAL SEA REALMENTE
         * UN ACTIVO RECOMENDADO
         * =================================================
         */
        List<Activo> recomendados
                = recomendacionActivoService
                        .obtenerRecomendados(
                                idColaborador
                        );

        boolean principalPermitido = false;

        for (Activo recomendado : recomendados) {

            if (recomendado
                    .getIdActivo()
                    .equals(idActivoPrincipal)) {

                principalPermitido = true;

                break;
            }
        }

        if (!principalPermitido) {

            throw new IllegalArgumentException(
                    "El activo principal seleccionado "
                    + "no corresponde a una recomendación "
                    + "válida para el cargo."
            );
        }

        /*
         * Recuperar activo principal.
         */
        Activo activoPrincipal
                = activoRepository
                        .findById(idActivoPrincipal)
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Activo principal no encontrado."
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
         * GUARDAR ACTIVO PRINCIPAL
         * =================================================
         */
        registrarAsignacionIndividual(
                colaborador,
                activoPrincipal,
                estadoAsignado,
                formulario.getObservaciones(),
                "Activo principal asignado a "
                + colaborador.getNombreCompleto()
        );

        /*
         * =================================================
         * COMPLEMENTARIOS
         * =================================================
         */
        if (idsComplementarios != null
                && !idsComplementarios.isEmpty()) {

            /*
             * Obtener los complementarios que el sistema
             * realmente recomienda para ese colaborador.
             */
            List<Activo> complementariosPermitidos
                    = recomendacionActivoService
                            .obtenerActivosComplementarios(
                                    idColaborador
                            );

            /*
             * Convertimos IDs permitidos en Set para
             * validar rápidamente cada selección.
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
             * También evitamos procesar dos veces
             * el mismo ID enviado desde el formulario.
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
                 * Verificar que sea un complemento
                 * permitido para ese cargo.
                 */
                if (!idsPermitidos.contains(
                        idComplementario)) {

                    throw new IllegalArgumentException(
                            "Uno de los activos complementarios "
                            + "seleccionados no corresponde "
                            + "a los requisitos del cargo."
                    );
                }

                /*
                 * Buscar activo.
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
                 * Comprobar que continúe disponible.
                 */
                validarActivoDisponible(
                        activoComplementario
                );

                /*
                 * Guardar nueva asignación.
                 */
                registrarAsignacionIndividual(
                        colaborador,
                        activoComplementario,
                        estadoAsignado,
                        formulario.getObservaciones(),
                        "Activo complementario asignado a "
                        + colaborador
                                .getNombreCompleto()
                );
            }
        }

        return "redirect:/asignaciones";
    }


    /*
     * =====================================================
     * REGISTRAR UNA ASIGNACIÓN INDIVIDUAL
     * =====================================================
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
