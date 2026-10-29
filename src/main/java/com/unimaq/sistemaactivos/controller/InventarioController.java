package com.unimaq.sistemaactivos.controller;

import com.unimaq.sistemaactivos.model.Activo;

import com.unimaq.sistemaactivos.repository.ActivoRepository;
import com.unimaq.sistemaactivos.repository.CentroCostoRepository;
import com.unimaq.sistemaactivos.repository.EstadoActivoRepository;
import com.unimaq.sistemaactivos.repository.MarcaRepository;
import com.unimaq.sistemaactivos.repository.SedeRepository;
import com.unimaq.sistemaactivos.repository.TipoEquipoRepository;
import com.unimaq.sistemaactivos.repository.UbicacionOperativaRepository;

import com.unimaq.sistemaactivos.service.HistorialActivoService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InventarioController {


    /*
     * =====================================================
     * REPOSITORIOS
     * =====================================================
     */
    private final ActivoRepository activoRepository;

    private final TipoEquipoRepository tipoEquipoRepository;

    private final MarcaRepository marcaRepository;

    private final EstadoActivoRepository estadoActivoRepository;

    private final SedeRepository sedeRepository;

    private final UbicacionOperativaRepository ubicacionRepository;

    private final CentroCostoRepository centroCostoRepository;

    private final HistorialActivoService historialActivoService;


    /*
     * =====================================================
     * CONSTRUCTOR
     * =====================================================
     */
    public InventarioController(
            ActivoRepository activoRepository,
            TipoEquipoRepository tipoEquipoRepository,
            MarcaRepository marcaRepository,
            EstadoActivoRepository estadoActivoRepository,
            SedeRepository sedeRepository,
            UbicacionOperativaRepository ubicacionRepository,
            CentroCostoRepository centroCostoRepository,
            HistorialActivoService historialActivoService) {

        this.activoRepository
                = activoRepository;

        this.tipoEquipoRepository
                = tipoEquipoRepository;

        this.marcaRepository
                = marcaRepository;

        this.estadoActivoRepository
                = estadoActivoRepository;

        this.sedeRepository
                = sedeRepository;

        this.ubicacionRepository
                = ubicacionRepository;

        this.centroCostoRepository
                = centroCostoRepository;

        this.historialActivoService
                = historialActivoService;
    }


    /*
     * =====================================================
     * LISTAR INVENTARIO
     * =====================================================
     */
    @GetMapping("/inventario")
    public String mostrarInventario(
            @RequestParam(
                    value = "q",
                    required = false
            ) String q,
            @RequestParam(
                    value = "tipoId",
                    required = false
            ) Integer tipoId,
            @RequestParam(
                    value = "marcaId",
                    required = false
            ) Integer marcaId,
            @RequestParam(
                    value = "estadoId",
                    required = false
            ) Integer estadoId,
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
         * Tamaños permitidos.
         */
        if (size != 10
                && size != 20
                && size != 50) {

            size = 10;
        }

        if (page < 0) {

            page = 0;
        }

        Pageable pageable
                = PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.DESC,
                                "idActivo"
                        )
                );

        Page<Activo> paginaActivos
                = activoRepository
                        .buscarConFiltros(
                                q,
                                tipoId,
                                marcaId,
                                estadoId,
                                sedeId,
                                pageable
                        );

        model.addAttribute(
                "activos",
                paginaActivos.getContent()
        );

        model.addAttribute(
                "paginaActivos",
                paginaActivos
        );


        /*
         * Catálogos para filtros.
         */
        model.addAttribute(
                "tiposEquipo",
                tipoEquipoRepository.findAll()
        );

        model.addAttribute(
                "marcas",
                marcaRepository.findAll()
        );

        model.addAttribute(
                "estados",
                estadoActivoRepository.findAll()
        );

        model.addAttribute(
                "sedes",
                sedeRepository.findAll()
        );


        /*
         * Mantener filtros seleccionados.
         */
        model.addAttribute(
                "q",
                q
        );

        model.addAttribute(
                "tipoId",
                tipoId
        );

        model.addAttribute(
                "marcaId",
                marcaId
        );

        model.addAttribute(
                "estadoId",
                estadoId
        );

        model.addAttribute(
                "sedeId",
                sedeId
        );

        model.addAttribute(
                "size",
                size
        );

        return "inventario";
    }


    /*
     * =====================================================
     * NUEVO ACTIVO
     * =====================================================
     */
    @GetMapping("/inventario/nuevo")
    public String mostrarFormularioNuevo(
            Model model) {

        model.addAttribute(
                "activo",
                new Activo()
        );

        cargarCatalogos(
                model
        );

        return "nuevo-activo";
    }


    /*
     * =====================================================
     * GUARDAR / ACTUALIZAR ACTIVO
     * =====================================================
     */
    @PostMapping("/inventario/guardar")
    public String guardarActivo(
            Activo formulario,
            @RequestParam("tipoEquipoId") Integer tipoEquipoId,
            @RequestParam("marcaId") Integer marcaId,
            @RequestParam("estadoId") Integer estadoId,
            @RequestParam(
                    value = "sedeId",
                    required = false
            ) Integer sedeId,
            @RequestParam(
                    value = "ubicacionId",
                    required = false
            ) Integer ubicacionId,
            @RequestParam(
                    value = "centroCostoId",
                    required = false
            ) Integer centroCostoId) {


        /*
         * =================================================
         * RECUPERAR RELACIONES REALES DE LA BASE DE DATOS
         * =================================================
         */
        var tipoEquipo
                = tipoEquipoRepository
                        .findById(
                                tipoEquipoId
                        )
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Tipo de equipo no encontrado."
                                )
                        );

        var marca
                = marcaRepository
                        .findById(
                                marcaId
                        )
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Marca no encontrada."
                                )
                        );

        var estado
                = estadoActivoRepository
                        .findById(
                                estadoId
                        )
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Estado no encontrado."
                                )
                        );


        /*
         * Relaciones opcionales.
         */
        var sede
                = sedeId != null
                        ? sedeRepository
                                .findById(
                                        sedeId
                                )
                                .orElseThrow(()
                                        -> new IllegalArgumentException(
                                                "Sede no encontrada."
                                        )
                                )
                        : null;

        var ubicacion
                = ubicacionId != null
                        ? ubicacionRepository
                                .findById(
                                        ubicacionId
                                )
                                .orElseThrow(()
                                        -> new IllegalArgumentException(
                                                "Ubicación no encontrada."
                                        )
                                )
                        : null;

        var centroCosto
                = centroCostoId != null
                        ? centroCostoRepository
                                .findById(
                                        centroCostoId
                                )
                                .orElseThrow(()
                                        -> new IllegalArgumentException(
                                                "Centro de costo no encontrado."
                                        )
                                )
                        : null;


        /*
         * =================================================
         * EDICIÓN
         * =================================================
         */
        if (formulario.getIdActivo()
                != null) {

            Activo existente
                    = activoRepository
                            .findById(
                                    formulario
                                            .getIdActivo()
                            )
                            .orElseThrow(()
                                    -> new IllegalArgumentException(
                                            "Activo no encontrado con ID: "
                                            + formulario
                                                    .getIdActivo()
                                    )
                            );


            /*
             * Identificación.
             */
            existente.setNumeroInventario(
                    formulario
                            .getNumeroInventario()
            );

            existente.setNumeroSerie(
                    formulario
                            .getNumeroSerie()
            );


            /*
             * Clasificación.
             */
            existente.setTipoEquipo(
                    tipoEquipo
            );

            existente.setMarca(
                    marca
            );

            existente.setModelo(
                    formulario
                            .getModelo()
            );


            /*
             * =================================================
             * CARACTERÍSTICAS TÉCNICAS
             * =================================================
             */
            existente.setProcesador(
                    formulario
                            .getProcesador()
            );

            existente.setGeneracionProcesador(
                    formulario
                            .getGeneracionProcesador()
            );

            existente.setRamGb(
                    formulario
                            .getRamGb()
            );

            existente.setAlmacenamientoGb(
                    formulario
                            .getAlmacenamientoGb()
            );


            /*
             * =================================================
             * FECHAS
             * =================================================
             */
            existente.setFechaCompra(
                    formulario
                            .getFechaCompra()
            );


            /*
             * Permite modificar o eliminar
             * la fecha de garantía.
             */
            existente.setFechaVencimientoGarantia(
                    formulario
                            .getFechaVencimientoGarantia()
            );


            /*
             * =================================================
             * ESTADO Y UBICACIÓN
             * =================================================
             */
            existente.setEstado(
                    estado
            );

            existente.setSede(
                    sede
            );

            existente.setUbicacion(
                    ubicacion
            );


            /*
             * =================================================
             * CENTRO DE COSTO
             * =================================================
             */
            existente.setCentroCosto(
                    centroCosto
            );


            /*
             * =================================================
             * OBSERVACIONES
             * =================================================
             */
            existente.setObservaciones(
                    formulario
                            .getObservaciones()
            );


            /*
             * =================================================
             * GUARDAR
             * =================================================
             */
            activoRepository.save(
                    existente
            );

        } else {


            /*
             * =================================================
             * NUEVO ACTIVO
             * =================================================
             */
            formulario.setTipoEquipo(
                    tipoEquipo
            );

            formulario.setMarca(
                    marca
            );

            formulario.setEstado(
                    estado
            );

            formulario.setSede(
                    sede
            );

            formulario.setUbicacion(
                    ubicacion
            );

            formulario.setCentroCosto(
                    centroCosto
            );

            activoRepository.save(
                    formulario
            );

        }

        return "redirect:/inventario";
    }


    /*
     * =====================================================
     * EDITAR ACTIVO
     * =====================================================
     */
    @GetMapping("/inventario/editar/{id}")
    public String editarActivo(
            @PathVariable("id") Integer id,
            Model model) {

        Activo activo
                = activoRepository
                        .findById(
                                id
                        )
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Activo no encontrado con ID: "
                                        + id
                                )
                        );

        model.addAttribute(
                "activo",
                activo
        );

        cargarCatalogos(
                model
        );

        return "editar-activo";
    }


    /*
     * =====================================================
     * VER ACTIVO
     * =====================================================
     */
    @GetMapping("/inventario/ver/{id}")
    public String verActivo(
            @PathVariable("id") Integer id,
            Model model) {

        Activo activo
                = activoRepository
                        .findById(
                                id
                        )
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Activo no encontrado con ID: "
                                        + id
                                )
                        );

        model.addAttribute(
                "activo",
                activo
        );

        model.addAttribute(
                "historial",
                historialActivoService
                        .obtenerHistorial(
                                id
                        )
        );

        return "ver-activo";
    }


    /*
     * =====================================================
     * CARGAR CATÁLOGOS
     * =====================================================
     */
    private void cargarCatalogos(
            Model model) {

        model.addAttribute(
                "tiposEquipo",
                tipoEquipoRepository.findAll()
        );

        model.addAttribute(
                "marcas",
                marcaRepository.findAll()
        );

        model.addAttribute(
                "estados",
                estadoActivoRepository.findAll()
        );

        model.addAttribute(
                "sedes",
                sedeRepository.findAll()
        );

        model.addAttribute(
                "ubicaciones",
                ubicacionRepository.findAll()
        );

        model.addAttribute(
                "centrosCosto",
                centroCostoRepository.findAll()
        );

    }

}
