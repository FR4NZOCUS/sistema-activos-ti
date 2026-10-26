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
     * INVENTARIO
     * FILTROS + PAGINACIÓN
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
         * =================================================
         * VALIDAR PÁGINA
         * =================================================
         */
        if (page < 0) {

            page = 0;
        }


        /*
         * =================================================
         * VALIDAR CANTIDAD DE REGISTROS
         * =================================================
         */
        if (size != 10
                && size != 20
                && size != 50) {

            size = 10;
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
                                "idActivo"
                        )
                );


        /*
         * =================================================
         * CONSULTA CON FILTROS
         * =================================================
         */
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


        /*
         * =================================================
         * VALIDAR PÁGINA EXISTENTE
         * =================================================
         */
        if (paginaActivos.getTotalPages() > 0
                && page
                >= paginaActivos.getTotalPages()) {

            page = 0;

            pageable
                    = PageRequest.of(
                            0,
                            size,
                            Sort.by(
                                    Sort.Direction.DESC,
                                    "idActivo"
                            )
                    );

            paginaActivos
                    = activoRepository
                            .buscarConFiltros(
                                    q,
                                    tipoId,
                                    marcaId,
                                    estadoId,
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
                "activos",
                paginaActivos.getContent()
        );

        model.addAttribute(
                "paginaActivos",
                paginaActivos
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
         * =================================================
         * CONSERVAR FILTROS
         * =================================================
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
     * GUARDAR / ACTUALIZAR
     * =====================================================
     */
    @PostMapping("/inventario/guardar")
    public String guardarActivo(
            Activo activo) {


        /*
         * =================================================
         * EDITAR ACTIVO EXISTENTE
         * =================================================
         */
        if (activo.getIdActivo()
                != null) {

            Activo existente
                    = activoRepository
                            .findById(
                                    activo
                                            .getIdActivo()
                            )
                            .orElseThrow(()
                                    -> new IllegalArgumentException(
                                            "Activo no encontrado con ID: "
                                            + activo.getIdActivo()
                                    )
                            );


            /*
             * =================================================
             * IDENTIFICACIÓN
             * =================================================
             */
            existente.setNumeroInventario(
                    activo
                            .getNumeroInventario()
            );

            existente.setNumeroSerie(
                    activo
                            .getNumeroSerie()
            );


            /*
             * =================================================
             * CLASIFICACIÓN
             * =================================================
             */
            existente.setTipoEquipo(
                    activo
                            .getTipoEquipo()
            );

            existente.setMarca(
                    activo
                            .getMarca()
            );

            existente.setModelo(
                    activo
                            .getModelo()
            );


            /*
             * =================================================
             * DATOS TÉCNICOS
             * =================================================
             */
            existente.setProcesador(
                    activo
                            .getProcesador()
            );

            existente.setGeneracionProcesador(
                    activo
                            .getGeneracionProcesador()
            );

            existente.setRamGb(
                    activo
                            .getRamGb()
            );

            existente.setAlmacenamientoGb(
                    activo
                            .getAlmacenamientoGb()
            );


            /*
             * =================================================
             * FECHA COMPRA
             * =================================================
             */
            existente.setFechaCompra(
                    activo
                            .getFechaCompra()
            );


            /*
             * =================================================
             * GARANTÍA
             * =================================================
             *
             * Si no llega una nueva fecha,
             * conservamos la existente.
             */
            if (activo
                    .getFechaVencimientoGarantia()
                    != null) {

                existente
                        .setFechaVencimientoGarantia(
                                activo
                                        .getFechaVencimientoGarantia()
                        );
            }


            /*
             * =================================================
             * ESTADO
             * =================================================
             */
            existente.setEstado(
                    activo
                            .getEstado()
            );


            /*
             * =================================================
             * SEDE
             * =================================================
             */
            existente.setSede(
                    activo
                            .getSede()
            );


            /*
             * =================================================
             * UBICACIÓN
             * =================================================
             */
            existente.setUbicacion(
                    activo
                            .getUbicacion()
            );


            /*
             * =================================================
             * CENTRO DE COSTO
             * =================================================
             */
            if (activo
                    .getCentroCosto()
                    != null) {

                existente.setCentroCosto(
                        activo
                                .getCentroCosto()
                );
            }


            /*
             * =================================================
             * OBSERVACIONES
             * =================================================
             */
            existente.setObservaciones(
                    activo
                            .getObservaciones()
            );


            /*
             * GUARDAR.
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
            activoRepository.save(
                    activo
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
                        .findById(id)
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
                        .findById(id)
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


        /*
         * HISTORIAL DEL ACTIVO.
         */
        model.addAttribute(
                "historial",
                historialActivoService
                        .obtenerHistorial(id)
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
