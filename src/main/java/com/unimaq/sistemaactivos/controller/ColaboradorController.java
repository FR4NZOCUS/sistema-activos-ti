package com.unimaq.sistemaactivos.controller;

import com.unimaq.sistemaactivos.model.Colaborador;

import com.unimaq.sistemaactivos.repository.CargoRepository;
import com.unimaq.sistemaactivos.repository.CentroCostoRepository;
import com.unimaq.sistemaactivos.repository.ColaboradorRepository;
import com.unimaq.sistemaactivos.repository.PuestoRepository;
import com.unimaq.sistemaactivos.repository.SedeRepository;
import com.unimaq.sistemaactivos.repository.UbicacionOperativaRepository;

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
public class ColaboradorController {


    /*
     * =====================================================
     * REPOSITORIOS
     * =====================================================
     */
    private final ColaboradorRepository colaboradorRepository;

    private final CargoRepository cargoRepository;

    private final PuestoRepository puestoRepository;

    private final CentroCostoRepository centroCostoRepository;

    private final SedeRepository sedeRepository;

    private final UbicacionOperativaRepository ubicacionOperativaRepository;


    /*
     * =====================================================
     * CONSTRUCTOR
     * =====================================================
     */
    public ColaboradorController(
            ColaboradorRepository colaboradorRepository,
            CargoRepository cargoRepository,
            PuestoRepository puestoRepository,
            CentroCostoRepository centroCostoRepository,
            SedeRepository sedeRepository,
            UbicacionOperativaRepository ubicacionOperativaRepository) {

        this.colaboradorRepository
                = colaboradorRepository;

        this.cargoRepository
                = cargoRepository;

        this.puestoRepository
                = puestoRepository;

        this.centroCostoRepository
                = centroCostoRepository;

        this.sedeRepository
                = sedeRepository;

        this.ubicacionOperativaRepository
                = ubicacionOperativaRepository;
    }


    /*
     * =====================================================
     * LISTADO DE COLABORADORES
     * FILTROS + PAGINACIÓN
     * =====================================================
     */
    @GetMapping("/colaboradores")
    public String listarColaboradores(
            @RequestParam(
                    value = "q",
                    required = false
            ) String q,
            @RequestParam(
                    value = "estado",
                    required = false
            ) String estado,
            @RequestParam(
                    value = "cargoId",
                    required = false
            ) Integer cargoId,
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
         * Validar página.
         */
        if (page < 0) {

            page = 0;
        }


        /*
         * Tamaños permitidos.
         */
        if (size != 10
                && size != 20
                && size != 50) {

            size = 10;
        }


        /*
         * Crear paginación.
         */
        Pageable pageable
                = PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.ASC,
                                "nombreCompleto"
                        )
                );


        /*
         * Consultar colaboradores.
         */
        Page<Colaborador> paginaColaboradores
                = colaboradorRepository
                        .buscarConFiltros(
                                q,
                                estado,
                                cargoId,
                                sedeId,
                                pageable
                        );


        /*
         * Si se solicita una página inexistente,
         * regresar a la primera página.
         */
        if (paginaColaboradores.getTotalPages() > 0
                && page
                >= paginaColaboradores.getTotalPages()) {

            page = 0;

            pageable
                    = PageRequest.of(
                            0,
                            size,
                            Sort.by(
                                    Sort.Direction.ASC,
                                    "nombreCompleto"
                            )
                    );

            paginaColaboradores
                    = colaboradorRepository
                            .buscarConFiltros(
                                    q,
                                    estado,
                                    cargoId,
                                    sedeId,
                                    pageable
                            );
        }


        /*
         * Resultados.
         */
        model.addAttribute(
                "colaboradores",
                paginaColaboradores.getContent()
        );

        model.addAttribute(
                "paginaColaboradores",
                paginaColaboradores
        );


        /*
         * Catálogos para filtros.
         */
        model.addAttribute(
                "cargos",
                cargoRepository.findAll()
        );

        model.addAttribute(
                "sedes",
                sedeRepository.findAll()
        );


        /*
         * Mantener filtros.
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
                "cargoId",
                cargoId
        );

        model.addAttribute(
                "sedeId",
                sedeId
        );

        model.addAttribute(
                "size",
                size
        );

        return "colaboradores";
    }


    /*
     * =====================================================
     * NUEVO COLABORADOR
     * =====================================================
     */
    @GetMapping("/colaboradores/nuevo")
    public String mostrarFormularioNuevo(
            Model model) {

        Colaborador colaborador
                = new Colaborador();


        /*
         * Estado por defecto.
         */
        colaborador.setEstadoColaborador(
                "ACTIVO"
        );

        model.addAttribute(
                "colaborador",
                colaborador
        );


        /*
         * Cargar:
         *
         * - puestos;
         * - perfiles tecnológicos;
         * - centros de costo;
         * - sedes;
         * - ubicaciones.
         */
        cargarCatalogos(
                model
        );

        return "nuevo-colaborador";
    }


    /*
     * =====================================================
     * GUARDAR COLABORADOR
     * =====================================================
     */
    @PostMapping("/colaboradores/guardar")
    public String guardarColaborador(
            Colaborador colaborador) {


        /*
         * =================================================
         * NOMBRE COMPLETO
         * =================================================
         */
        String nombres
                = colaborador.getNombres() != null
                ? colaborador
                        .getNombres()
                        .trim()
                : "";

        String apellidoPaterno
                = colaborador.getApellidoPaterno() != null
                ? colaborador
                        .getApellidoPaterno()
                        .trim()
                : "";

        String apellidoMaterno
                = colaborador.getApellidoMaterno() != null
                ? colaborador
                        .getApellidoMaterno()
                        .trim()
                : "";

        String nombreCompleto
                = (nombres
                        + " "
                        + apellidoPaterno
                        + " "
                        + apellidoMaterno)
                        .trim()
                        .replaceAll(
                                "\\s+",
                                " "
                        )
                        .toUpperCase();

        colaborador.setNombreCompleto(
                nombreCompleto
        );


        /*
         * =================================================
         * CÓDIGO DE USUARIO
         * =================================================
         */
        if (colaborador.getCodigoUsuario() != null) {

            colaborador.setCodigoUsuario(
                    colaborador
                            .getCodigoUsuario()
                            .trim()
            );
        }


        /*
         * =================================================
         * ESTADO
         * =================================================
         */
        if (colaborador.getEstadoColaborador() == null

                || colaborador
                        .getEstadoColaborador()
                        .isBlank()) {

            colaborador.setEstadoColaborador(
                    "ACTIVO"
            );
        }


        /*
         * =================================================
         * PUESTO REAL
         * =================================================
         */
        if (colaborador.getPuesto() != null

                && colaborador
                        .getPuesto()
                        .getIdPuesto()
                != null) {

            colaborador.setPuesto(
                    puestoRepository
                            .findById(
                                    colaborador
                                            .getPuesto()
                                            .getIdPuesto()
                            )
                            .orElseThrow(()
                                    -> new IllegalArgumentException(
                                            "Puesto no encontrado."
                                    )
                            )
            );

        } else {


            /*
             * Puesto real es opcional.
             */
            colaborador.setPuesto(
                    null
            );
        }


        /*
         * =================================================
         * PERFIL TECNOLÓGICO
         * =================================================
         */
        if (colaborador.getCargo() != null

                && colaborador
                        .getCargo()
                        .getIdCargo()
                != null) {

            colaborador.setCargo(
                    cargoRepository
                            .findById(
                                    colaborador
                                            .getCargo()
                                            .getIdCargo()
                            )
                            .orElseThrow(()
                                    -> new IllegalArgumentException(
                                            "Perfil tecnológico no encontrado."
                                    )
                            )
            );

        } else {

            colaborador.setCargo(
                    null
            );
        }


        /*
         * =================================================
         * CENTRO DE COSTO
         * =================================================
         */
        if (colaborador.getCentroCosto() != null

                && colaborador
                        .getCentroCosto()
                        .getIdCentroCosto()
                != null) {

            colaborador.setCentroCosto(
                    centroCostoRepository
                            .findById(
                                    colaborador
                                            .getCentroCosto()
                                            .getIdCentroCosto()
                            )
                            .orElseThrow(()
                                    -> new IllegalArgumentException(
                                            "Centro de costo no encontrado."
                                    )
                            )
            );

        } else {

            colaborador.setCentroCosto(
                    null
            );
        }


        /*
         * =================================================
         * SEDE
         * =================================================
         */
        if (colaborador.getSede() != null

                && colaborador
                        .getSede()
                        .getIdSede()
                != null) {

            colaborador.setSede(
                    sedeRepository
                            .findById(
                                    colaborador
                                            .getSede()
                                            .getIdSede()
                            )
                            .orElseThrow(()
                                    -> new IllegalArgumentException(
                                            "Sede no encontrada."
                                    )
                            )
            );

        } else {

            colaborador.setSede(
                    null
            );
        }


        /*
         * =================================================
         * UBICACIÓN
         * =================================================
         */
        if (colaborador.getUbicacion() != null

                && colaborador
                        .getUbicacion()
                        .getIdUbicacion()
                != null) {

            colaborador.setUbicacion(
                    ubicacionOperativaRepository
                            .findById(
                                    colaborador
                                            .getUbicacion()
                                            .getIdUbicacion()
                            )
                            .orElseThrow(()
                                    -> new IllegalArgumentException(
                                            "Ubicación no encontrada."
                                    )
                            )
            );

        } else {

            colaborador.setUbicacion(
                    null
            );
        }


        /*
         * =================================================
         * GUARDAR
         * =================================================
         */
        colaboradorRepository.save(
                colaborador
        );

        return "redirect:/colaboradores";
    }


    /*
     * =====================================================
     * EDITAR COLABORADOR
     * =====================================================
     */
    @GetMapping("/colaboradores/editar/{id}")
    public String editarColaborador(
            @PathVariable("id") Integer id,
            Model model) {

        Colaborador colaborador
                = colaboradorRepository
                        .findById(
                                id
                        )
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Colaborador no encontrado "
                                        + "con ID: "
                                        + id
                                )
                        );

        model.addAttribute(
                "colaborador",
                colaborador
        );


        /*
         * Importante:
         *
         * También cargar puestos al editar
         * para que el combo pueda mostrar
         * todos los registros.
         */
        cargarCatalogos(
                model
        );

        return "nuevo-colaborador";
    }


    /*
     * =====================================================
     * VER COLABORADOR
     * =====================================================
     */
    @GetMapping("/colaboradores/ver/{id}")
    public String verColaborador(
            @PathVariable("id") Integer id,
            Model model) {

        Colaborador colaborador
                = colaboradorRepository
                        .findById(
                                id
                        )
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Colaborador no encontrado "
                                        + "con ID: "
                                        + id
                                )
                        );

        model.addAttribute(
                "colaborador",
                colaborador
        );

        return "ver-colaborador";
    }


    /*
     * =====================================================
     * CARGAR CATÁLOGOS
     * =====================================================
     */
    private void cargarCatalogos(
            Model model) {


        /*
         * =================================================
         * PUESTOS REALES
         * =================================================
         *
         * Este atributo es el que faltaba.
         *
         * El HTML utiliza:
         *
         * ${puestos}
         */
        model.addAttribute(
                "puestos",
                puestoRepository.findAll()
        );


        /*
         * =================================================
         * PERFILES TECNOLÓGICOS
         * =================================================
         */
        model.addAttribute(
                "cargos",
                cargoRepository.findAll()
        );


        /*
         * =================================================
         * CENTROS DE COSTO
         * =================================================
         */
        model.addAttribute(
                "centrosCosto",
                centroCostoRepository.findAll()
        );


        /*
         * =================================================
         * SEDES
         * =================================================
         */
        model.addAttribute(
                "sedes",
                sedeRepository.findAll()
        );


        /*
         * =================================================
         * UBICACIONES
         * =================================================
         */
        model.addAttribute(
                "ubicaciones",
                ubicacionOperativaRepository.findAll()
        );
    }
}
