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

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
            Model model) {

        model.addAttribute(
                "activos",
                activoRepository.findAll()
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

        cargarCatalogos(model);

        return "nuevo-activo";
    }


    /*
     * =====================================================
     * GUARDAR / ACTUALIZAR ACTIVO
     * =====================================================
     */
    @PostMapping("/inventario/guardar")
    public String guardarActivo(
            Activo activo) {


        /*
         * =================================================
         * EDICIÓN
         * =================================================
         *
         * Si existe idActivo significa que estamos
         * modificando un registro ya existente.
         *
         * MUY IMPORTANTE:
         *
         * El estado del activo NO se obtiene del formulario.
         *
         * Siempre conservamos el estado almacenado
         * actualmente en la base de datos.
         */
        if (activo.getIdActivo() != null) {


            /*
             * Recuperar activo real.
             */
            Activo existente
                    = activoRepository
                            .findById(
                                    activo.getIdActivo()
                            )
                            .orElseThrow(()
                                    -> new IllegalArgumentException(
                                            "Activo no encontrado con ID: "
                                            + activo.getIdActivo()
                                    )
                            );


            /*
             * =================================================
             * NORMALIZAR RELACIONES EDITABLES
             * =================================================
             *
             * No normalizamos el estado porque no puede
             * modificarse desde la edición general.
             */
            normalizarRelacionesEditables(
                    activo
            );


            /*
             * =================================================
             * IDENTIFICACIÓN
             * =================================================
             */
            existente.setNumeroInventario(
                    activo.getNumeroInventario()
            );

            existente.setNumeroSerie(
                    activo.getNumeroSerie()
            );


            /*
             * =================================================
             * CLASIFICACIÓN
             * =================================================
             */
            existente.setTipoEquipo(
                    activo.getTipoEquipo()
            );

            existente.setMarca(
                    activo.getMarca()
            );

            existente.setModelo(
                    activo.getModelo()
            );


            /*
             * =================================================
             * CARACTERÍSTICAS TÉCNICAS
             * =================================================
             */
            existente.setProcesador(
                    activo.getProcesador()
            );

            existente.setGeneracionProcesador(
                    activo.getGeneracionProcesador()
            );

            existente.setRamGb(
                    activo.getRamGb()
            );

            existente.setAlmacenamientoGb(
                    activo.getAlmacenamientoGb()
            );


            /*
             * =================================================
             * FECHAS
             * =================================================
             */
            existente.setFechaCompra(
                    activo.getFechaCompra()
            );

            existente.setFechaVencimientoGarantia(
                    activo.getFechaVencimientoGarantia()
            );


            /*
             * =================================================
             * ESTADO
             * =================================================
             *
             * NO hacemos:
             *
             * existente.setEstado(activo.getEstado());
             *
             * El estado actual permanece exactamente igual.
             *
             * Esto evita cambios manuales como:
             *
             * ASIGNADO_EN_USO -> DISPONIBLE
             * MANTENIMIENTO -> DISPONIBLE
             * BAJA -> ASIGNADO_EN_USO
             *
             * Los cambios de estado solamente deben realizarse
             * mediante los procesos correspondientes.
             */
 /*
             * =================================================
             * UBICACIÓN
             * =================================================
             */
            existente.setSede(
                    activo.getSede()
            );

            existente.setUbicacion(
                    activo.getUbicacion()
            );

            existente.setCentroCosto(
                    activo.getCentroCosto()
            );


            /*
             * =================================================
             * OBSERVACIONES
             * =================================================
             */
            existente.setObservaciones(
                    activo.getObservaciones()
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
             *
             * Para un activo nuevo sí necesitamos definir
             * un estado inicial.
             */
            normalizarRelacionesNuevoActivo(
                    activo
            );

            activoRepository.save(
                    activo
            );
        }

        return "redirect:/inventario";
    }


    /*
     * =====================================================
     * NORMALIZAR RELACIONES DE EDICIÓN
     * =====================================================
     *
     * Convierte las relaciones recibidas desde Thymeleaf
     * en entidades reales administradas por JPA.
     *
     * En edición NO se procesa el estado.
     */
    private void normalizarRelacionesEditables(
            Activo activo) {


        /*
         * =================================================
         * TIPO DE EQUIPO
         * =================================================
         */
        if (activo.getTipoEquipo() == null
                || activo
                        .getTipoEquipo()
                        .getIdTipoEquipo() == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un tipo de equipo."
            );
        }

        Integer idTipoEquipo
                = activo
                        .getTipoEquipo()
                        .getIdTipoEquipo();

        activo.setTipoEquipo(
                tipoEquipoRepository
                        .findById(idTipoEquipo)
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Tipo de equipo no encontrado."
                                )
                        )
        );


        /*
         * =================================================
         * MARCA
         * =================================================
         */
        if (activo.getMarca() == null
                || activo
                        .getMarca()
                        .getIdMarca() == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar una marca."
            );
        }

        Integer idMarca
                = activo
                        .getMarca()
                        .getIdMarca();

        activo.setMarca(
                marcaRepository
                        .findById(idMarca)
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Marca no encontrada."
                                )
                        )
        );


        /*
         * =================================================
         * SEDE
         * =================================================
         */
        if (activo.getSede() != null
                && activo
                        .getSede()
                        .getIdSede() != null) {

            Integer idSede
                    = activo
                            .getSede()
                            .getIdSede();

            activo.setSede(
                    sedeRepository
                            .findById(idSede)
                            .orElseThrow(()
                                    -> new IllegalArgumentException(
                                            "Sede no encontrada."
                                    )
                            )
            );

        } else {

            activo.setSede(null);
        }


        /*
         * =================================================
         * UBICACIÓN
         * =================================================
         */
        if (activo.getUbicacion() != null
                && activo
                        .getUbicacion()
                        .getIdUbicacion() != null) {

            Integer idUbicacion
                    = activo
                            .getUbicacion()
                            .getIdUbicacion();

            activo.setUbicacion(
                    ubicacionRepository
                            .findById(idUbicacion)
                            .orElseThrow(()
                                    -> new IllegalArgumentException(
                                            "Ubicación no encontrada."
                                    )
                            )
            );

        } else {

            activo.setUbicacion(null);
        }


        /*
         * =================================================
         * CENTRO DE COSTO
         * =================================================
         */
        if (activo.getCentroCosto() != null
                && activo
                        .getCentroCosto()
                        .getIdCentroCosto() != null) {

            Integer idCentroCosto
                    = activo
                            .getCentroCosto()
                            .getIdCentroCosto();

            activo.setCentroCosto(
                    centroCostoRepository
                            .findById(idCentroCosto)
                            .orElseThrow(()
                                    -> new IllegalArgumentException(
                                            "Centro de costo no encontrado."
                                    )
                            )
            );

        } else {

            activo.setCentroCosto(null);
        }
    }


    /*
     * =====================================================
     * NORMALIZAR NUEVO ACTIVO
     * =====================================================
     *
     * Un activo nuevo necesita las mismas relaciones
     * editables, pero además requiere un estado inicial.
     */
    private void normalizarRelacionesNuevoActivo(
            Activo activo) {


        /*
         * Primero normalizamos:
         *
         * - tipo
         * - marca
         * - sede
         * - ubicación
         * - centro de costo
         */
        normalizarRelacionesEditables(
                activo
        );


        /*
         * =================================================
         * ESTADO INICIAL
         * =================================================
         */
        if (activo.getEstado() == null
                || activo
                        .getEstado()
                        .getIdEstado() == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un estado inicial."
            );
        }

        Integer idEstado
                = activo
                        .getEstado()
                        .getIdEstado();

        activo.setEstado(
                estadoActivoRepository
                        .findById(idEstado)
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Estado del activo no encontrado."
                                )
                        )
        );
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

        cargarCatalogos(model);

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
