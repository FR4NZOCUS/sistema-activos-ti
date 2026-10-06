package com.unimaq.sistemaactivos.controller;

import com.unimaq.sistemaactivos.model.Colaborador;

import com.unimaq.sistemaactivos.repository.CargoRepository;
import com.unimaq.sistemaactivos.repository.CentroCostoRepository;
import com.unimaq.sistemaactivos.repository.ColaboradorRepository;
import com.unimaq.sistemaactivos.repository.PuestoRepository;
import com.unimaq.sistemaactivos.repository.SedeRepository;
import com.unimaq.sistemaactivos.repository.UbicacionOperativaRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ColaboradorController {

    private final ColaboradorRepository colaboradorRepository;
    private final PuestoRepository puestoRepository;
    private final CargoRepository cargoRepository;
    private final CentroCostoRepository centroCostoRepository;
    private final SedeRepository sedeRepository;
    private final UbicacionOperativaRepository ubicacionRepository;

    public ColaboradorController(
            ColaboradorRepository colaboradorRepository,
            PuestoRepository puestoRepository,
            CargoRepository cargoRepository,
            CentroCostoRepository centroCostoRepository,
            SedeRepository sedeRepository,
            UbicacionOperativaRepository ubicacionRepository) {

        this.colaboradorRepository = colaboradorRepository;
        this.puestoRepository = puestoRepository;
        this.cargoRepository = cargoRepository;
        this.centroCostoRepository = centroCostoRepository;
        this.sedeRepository = sedeRepository;
        this.ubicacionRepository = ubicacionRepository;
    }


    /*
     * =====================================================
     * LISTADO DE COLABORADORES
     * =====================================================
     */
    @GetMapping("/colaboradores")
    public String listarColaboradores(
            Model model) {

        model.addAttribute(
                "colaboradores",
                colaboradorRepository.findAll()
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
         * Estado inicial por defecto.
         */
        colaborador.setEstadoColaborador(
                "ACTIVO"
        );

        model.addAttribute(
                "colaborador",
                colaborador
        );

        cargarCatalogos(model);

        return "nuevo-colaborador";
    }


    /*
     * =====================================================
     * GUARDAR COLABORADOR
     * =====================================================
     *
     * Este método se utiliza tanto para:
     *
     * - crear;
     * - actualizar.
     */
    @PostMapping("/colaboradores/guardar")
    public String guardarColaborador(
            Colaborador colaborador) {


        /*
         * =================================================
         * CONSTRUIR NOMBRE COMPLETO
         * =================================================
         */
        if (colaborador.getNombres() != null) {

            String nombreCompleto
                    = colaborador.getNombres()
                    + " "
                    + (colaborador.getApellidoPaterno() != null
                    ? colaborador.getApellidoPaterno()
                    : "")
                    + " "
                    + (colaborador.getApellidoMaterno() != null
                    ? colaborador.getApellidoMaterno()
                    : "");

            colaborador.setNombreCompleto(
                    nombreCompleto
                            .trim()
                            .toUpperCase()
            );
        }


        /*
         * =================================================
         * ESTADO POR DEFECTO
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
         *
         * Si el formulario envía una relación vacía,
         * se convierte en NULL.
         */
        if (colaborador.getPuesto() != null
                && colaborador
                        .getPuesto()
                        .getIdPuesto() == null) {

            colaborador.setPuesto(null);
        }


        /*
         * =================================================
         * PERFIL TECNOLÓGICO
         * =================================================
         *
         * El Cargo representa actualmente el perfil
         * tecnológico utilizado por el motor de
         * asignación automática.
         */
        if (colaborador.getCargo() != null
                && colaborador
                        .getCargo()
                        .getIdCargo() == null) {

            colaborador.setCargo(null);
        }


        /*
         * =================================================
         * CENTRO DE COSTO
         * =================================================
         */
        if (colaborador.getCentroCosto() != null
                && colaborador
                        .getCentroCosto()
                        .getIdCentroCosto() == null) {

            colaborador.setCentroCosto(null);
        }


        /*
         * =================================================
         * SEDE
         * =================================================
         */
        if (colaborador.getSede() != null
                && colaborador
                        .getSede()
                        .getIdSede() == null) {

            colaborador.setSede(null);
        }


        /*
         * =================================================
         * UBICACIÓN
         * =================================================
         */
        if (colaborador.getUbicacion() != null
                && colaborador
                        .getUbicacion()
                        .getIdUbicacion() == null) {

            colaborador.setUbicacion(null);
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
                        .findById(id)
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

        cargarCatalogos(model);

        return "editar-colaborador";
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
                        .findById(id)
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
     *
     * Evitamos repetir las mismas consultas en los
     * formularios nuevo y editar.
     */
    private void cargarCatalogos(
            Model model) {


        /*
         * Puestos reales.
         */
        model.addAttribute(
                "puestos",
                puestoRepository.findAll()
        );


        /*
         * Perfiles tecnológicos.
         */
        model.addAttribute(
                "cargos",
                cargoRepository.findAll()
        );


        /*
         * Centros de costo.
         */
        model.addAttribute(
                "centrosCosto",
                centroCostoRepository.findAll()
        );


        /*
         * Sedes.
         */
        model.addAttribute(
                "sedes",
                sedeRepository.findAll()
        );


        /*
         * Ubicaciones.
         */
        model.addAttribute(
                "ubicaciones",
                ubicacionRepository.findAll()
        );
    }
}
