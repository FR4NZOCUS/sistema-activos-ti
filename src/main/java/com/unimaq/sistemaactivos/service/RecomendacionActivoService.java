package com.unimaq.sistemaactivos.service;

import com.unimaq.sistemaactivos.model.Activo;
import com.unimaq.sistemaactivos.model.Colaborador;
import com.unimaq.sistemaactivos.model.RequisitoCargo;

import com.unimaq.sistemaactivos.repository.ActivoRepository;
import com.unimaq.sistemaactivos.repository.ColaboradorRepository;
import com.unimaq.sistemaactivos.repository.RequisitoCargoRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class RecomendacionActivoService {

    private final ActivoRepository activoRepository;

    private final ColaboradorRepository colaboradorRepository;

    private final RequisitoCargoRepository requisitoCargoRepository;

    public RecomendacionActivoService(
            ActivoRepository activoRepository,
            ColaboradorRepository colaboradorRepository,
            RequisitoCargoRepository requisitoCargoRepository) {

        this.activoRepository
                = activoRepository;

        this.colaboradorRepository
                = colaboradorRepository;

        this.requisitoCargoRepository
                = requisitoCargoRepository;
    }


    /*
     * =====================================================
     * OBTENER EQUIPOS RECOMENDADOS
     * =====================================================
     */
    public List<Activo> obtenerRecomendados(
            Integer idColaborador) {

        Colaborador colaborador
                = buscarColaborador(
                        idColaborador
                );


        /*
         * El colaborador debe tener cargo.
         */
        if (colaborador.getCargo() == null) {

            return new ArrayList<>();
        }


        /*
         * =================================================
         * REQUISITOS PRINCIPALES DEL CARGO
         * =================================================
         */
        List<RequisitoCargo> requisitosPrincipales
                = requisitoCargoRepository
                        .findByCargoIdCargoAndTipoRequisito(
                                colaborador
                                        .getCargo()
                                        .getIdCargo(),
                                "PRINCIPAL"
                        );


        /*
         * Si el cargo no tiene un requisito PRINCIPAL,
         * no podemos seleccionar automáticamente.
         */
        if (requisitosPrincipales == null
                || requisitosPrincipales.isEmpty()) {

            return new ArrayList<>();
        }


        /*
         * =================================================
         * EQUIPOS DISPONIBLES
         * =================================================
         */
        List<Activo> disponibles
                = activoRepository
                        .findByEstadoNombreEstado(
                                "DISPONIBLE"
                        );

        if (disponibles == null
                || disponibles.isEmpty()) {

            return new ArrayList<>();
        }


        /*
         * =================================================
         * FILTRAR EQUIPOS COMPATIBLES
         * =================================================
         */
        List<Activo> recomendados
                = filtrarActivos(
                        disponibles,
                        requisitosPrincipales
                );


        /*
         * Ordenar de mejor a peor opción.
         */
        ordenarActivosPrincipales(
                recomendados,
                requisitosPrincipales
        );

        return recomendados;
    }


    /*
     * =====================================================
     * ACTIVO PRINCIPAL AUTOMÁTICO
     * =====================================================
     */
    public Activo obtenerActivoPrincipalAutomatico(
            Integer idColaborador) {

        List<Activo> recomendados
                = obtenerRecomendados(
                        idColaborador
                );

        if (recomendados == null
                || recomendados.isEmpty()) {

            return null;
        }


        /*
         * El primero representa el equipo
         * más adecuado.
         */
        return recomendados.get(0);
    }


    /*
     * =====================================================
     * DIAGNÓSTICO DE LA RECOMENDACIÓN
     * =====================================================
     *
     * Permite distinguir entre:
     *
     * - colaborador sin cargo;
     * - cargo sin requisito PRINCIPAL;
     * - falta de stock;
     * - equipos disponibles pero incompatibles;
     * - equipo encontrado correctamente.
     */
    public String obtenerDiagnostico(
            Integer idColaborador) {

        Colaborador colaborador
                = buscarColaborador(
                        idColaborador
                );


        /*
         * =================================================
         * SIN CARGO
         * =================================================
         */
        if (colaborador.getCargo() == null) {

            return "El colaborador no tiene un cargo asignado.";
        }


        /*
         * =================================================
         * BUSCAR REQUISITOS
         * =================================================
         */
        List<RequisitoCargo> requisitos
                = requisitoCargoRepository
                        .findByCargoIdCargoAndTipoRequisito(
                                colaborador
                                        .getCargo()
                                        .getIdCargo(),
                                "PRINCIPAL"
                        );


        /*
         * =================================================
         * CARGO SIN REQUISITO PRINCIPAL
         * =================================================
         */
        if (requisitos == null
                || requisitos.isEmpty()) {

            return "El cargo "
                    + colaborador
                            .getCargo()
                            .getNombreCargo()
                    + " no tiene un requisito PRINCIPAL configurado.";
        }


        /*
         * =================================================
         * BUSCAR STOCK DISPONIBLE
         * =================================================
         */
        List<Activo> disponibles
                = activoRepository
                        .findByEstadoNombreEstado(
                                "DISPONIBLE"
                        );


        /*
         * =================================================
         * SIN STOCK
         * =================================================
         */
        if (disponibles == null
                || disponibles.isEmpty()) {

            return "Actualmente no existen activos "
                    + "con estado DISPONIBLE.";
        }


        /*
         * =================================================
         * COMPROBAR COMPATIBILIDAD
         * =================================================
         */
        List<Activo> recomendados
                = filtrarActivos(
                        disponibles,
                        requisitos
                );

        if (recomendados.isEmpty()) {

            return "Existen "
                    + disponibles.size()
                    + " activos DISPONIBLES, pero ninguno "
                    + "cumple completamente los requisitos "
                    + "del cargo "
                    + colaborador
                            .getCargo()
                            .getNombreCargo()
                    + ". Revise tipo de equipo, procesador, "
                    + "generación, RAM y almacenamiento.";
        }


        /*
         * Todo correcto.
         */
        return null;
    }


    /*
     * =====================================================
     * REQUISITOS COMPLEMENTARIOS
     * =====================================================
     */
    public List<RequisitoCargo> obtenerComplementarios(
            Integer idColaborador) {

        Colaborador colaborador
                = buscarColaborador(
                        idColaborador
                );

        if (colaborador.getCargo() == null) {

            return new ArrayList<>();
        }

        return requisitoCargoRepository
                .findByCargoIdCargoAndTipoRequisito(
                        colaborador
                                .getCargo()
                                .getIdCargo(),
                        "COMPLEMENTARIO"
                );
    }


    /*
     * =====================================================
     * ACTIVOS COMPLEMENTARIOS
     * =====================================================
     */
    public List<Activo> obtenerActivosComplementarios(
            Integer idColaborador) {

        Colaborador colaborador
                = buscarColaborador(
                        idColaborador
                );

        if (colaborador.getCargo() == null) {

            return new ArrayList<>();
        }

        List<RequisitoCargo> requisitosComplementarios
                = requisitoCargoRepository
                        .findByCargoIdCargoAndTipoRequisito(
                                colaborador
                                        .getCargo()
                                        .getIdCargo(),
                                "COMPLEMENTARIO"
                        );


        /*
         * Si no existen requisitos complementarios,
         * devolver lista vacía.
         */
        if (requisitosComplementarios == null
                || requisitosComplementarios.isEmpty()) {

            return new ArrayList<>();
        }

        List<Activo> disponibles
                = activoRepository
                        .findByEstadoNombreEstado(
                                "DISPONIBLE"
                        );

        return filtrarActivos(
                disponibles,
                requisitosComplementarios
        );
    }


    /*
     * =====================================================
     * FILTRAR ACTIVOS
     * =====================================================
     */
    private List<Activo> filtrarActivos(
            List<Activo> activos,
            List<RequisitoCargo> requisitos) {

        List<Activo> resultado
                = new ArrayList<>();

        if (activos == null
                || activos.isEmpty()) {

            return resultado;
        }

        if (requisitos == null
                || requisitos.isEmpty()) {

            return resultado;
        }


        /*
         * Evaluar cada activo.
         */
        for (Activo activo : activos) {

            for (RequisitoCargo requisito
                    : requisitos) {

                if (cumpleRequisito(
                        activo,
                        requisito)) {

                    resultado.add(
                            activo
                    );


                    /*
                     * No agregar el mismo equipo
                     * más de una vez.
                     */
                    break;
                }
            }
        }

        return resultado;
    }


    /*
     * =====================================================
     * COMPROBAR REQUISITO
     * =====================================================
     */
    private boolean cumpleRequisito(
            Activo activo,
            RequisitoCargo requisito) {


        /*
         * =================================================
         * TIPO DE EQUIPO
         * =================================================
         */
        if (activo.getTipoEquipo() == null
                || requisito.getTipoEquipo() == null) {

            return false;
        }

        if (activo
                .getTipoEquipo()
                .getIdTipoEquipo() == null
                || requisito
                        .getTipoEquipo()
                        .getIdTipoEquipo() == null) {

            return false;
        }

        if (!activo
                .getTipoEquipo()
                .getIdTipoEquipo()
                .equals(
                        requisito
                                .getTipoEquipo()
                                .getIdTipoEquipo()
                )) {

            return false;
        }


        /*
         * =================================================
         * PROCESADOR
         * =================================================
         */
        if (requisito.getProcesadorMinimo() != null
                && !requisito
                        .getProcesadorMinimo()
                        .isBlank()) {

            if (!cumpleProcesador(
                    activo.getProcesador(),
                    requisito.getProcesadorMinimo()
            )) {

                return false;
            }
        }


        /*
         * =================================================
         * GENERACIÓN
         * =================================================
         */
        if (requisito.getGeneracionMinima()
                != null) {

            if (activo.getGeneracionProcesador()
                    == null) {

                return false;
            }

            if (activo.getGeneracionProcesador()
                    < requisito.getGeneracionMinima()) {

                return false;
            }
        }


        /*
         * =================================================
         * RAM
         * =================================================
         */
        if (requisito.getRamMinimaGb()
                != null) {

            if (activo.getRamGb()
                    == null) {

                return false;
            }

            if (activo.getRamGb()
                    < requisito.getRamMinimaGb()) {

                return false;
            }
        }


        /*
         * =================================================
         * ALMACENAMIENTO
         * =================================================
         */
        if (requisito.getAlmacenamientoMinimoGb()
                != null) {

            if (activo.getAlmacenamientoGb()
                    == null) {

                return false;
            }

            if (activo.getAlmacenamientoGb()
                    < requisito
                            .getAlmacenamientoMinimoGb()) {

                return false;
            }
        }


        /*
         * Cumple todos los criterios.
         */
        return true;
    }


    /*
     * =====================================================
     * ORDENAR ACTIVOS PRINCIPALES
     * =====================================================
     */
    private void ordenarActivosPrincipales(
            List<Activo> activos,
            List<RequisitoCargo> requisitos) {

        if (activos == null
                || activos.isEmpty()) {

            return;
        }

        if (requisitos == null
                || requisitos.isEmpty()) {

            return;
        }

        RequisitoCargo requisitoReferencia
                = requisitos.get(0);

        activos.sort(
                Comparator
                        /*
                         * Generación.
                         */
                        .comparing(
                                (Activo activo)
                                -> valorGeneracionOrden(
                                        activo,
                                        requisitoReferencia
                                )
                        )
                        /*
                         * Procesador.
                         */
                        .thenComparing(
                                activo
                                -> valorProcesadorOrden(
                                        activo,
                                        requisitoReferencia
                                )
                        )
                        /*
                         * RAM.
                         */
                        .thenComparing(
                                activo
                                -> valorRamOrden(
                                        activo,
                                        requisitoReferencia
                                )
                        )
                        /*
                         * Almacenamiento.
                         */
                        .thenComparing(
                                activo
                                -> valorAlmacenamientoOrden(
                                        activo,
                                        requisitoReferencia
                                )
                        )
                        /*
                         * ID.
                         */
                        .thenComparing(
                                activo
                                -> activo.getIdActivo() != null
                                ? activo.getIdActivo()
                                : Integer.MAX_VALUE
                        )
        );
    }


    /*
     * =====================================================
     * ORDEN GENERACIÓN
     * =====================================================
     */
    private int valorGeneracionOrden(
            Activo activo,
            RequisitoCargo requisito) {

        if (activo.getGeneracionProcesador()
                == null) {

            return Integer.MAX_VALUE;
        }

        if (requisito.getGeneracionMinima()
                == null) {

            return activo
                    .getGeneracionProcesador();
        }

        return activo
                .getGeneracionProcesador()
                - requisito
                        .getGeneracionMinima();
    }


    /*
     * =====================================================
     * ORDEN PROCESADOR
     * =====================================================
     */
    private int valorProcesadorOrden(
            Activo activo,
            RequisitoCargo requisito) {

        int nivelActivo
                = obtenerNivelProcesador(
                        activo.getProcesador()
                );

        int nivelRequisito
                = obtenerNivelProcesador(
                        requisito.getProcesadorMinimo()
                );

        if (nivelActivo == 0) {

            return Integer.MAX_VALUE;
        }

        if (nivelRequisito == 0) {

            return nivelActivo;
        }

        return nivelActivo
                - nivelRequisito;
    }


    /*
     * =====================================================
     * ORDEN RAM
     * =====================================================
     */
    private int valorRamOrden(
            Activo activo,
            RequisitoCargo requisito) {

        if (activo.getRamGb()
                == null) {

            return Integer.MAX_VALUE;
        }

        if (requisito.getRamMinimaGb()
                == null) {

            return activo.getRamGb();
        }

        return activo.getRamGb()
                - requisito.getRamMinimaGb();
    }


    /*
     * =====================================================
     * ORDEN ALMACENAMIENTO
     * =====================================================
     */
    private int valorAlmacenamientoOrden(
            Activo activo,
            RequisitoCargo requisito) {

        if (activo.getAlmacenamientoGb()
                == null) {

            return Integer.MAX_VALUE;
        }

        if (requisito.getAlmacenamientoMinimoGb()
                == null) {

            return activo
                    .getAlmacenamientoGb();
        }

        return activo
                .getAlmacenamientoGb()
                - requisito
                        .getAlmacenamientoMinimoGb();
    }


    /*
     * =====================================================
     * BUSCAR COLABORADOR
     * =====================================================
     */
    private Colaborador buscarColaborador(
            Integer idColaborador) {

        return colaboradorRepository
                .findById(
                        idColaborador
                )
                .orElseThrow(()
                        -> new IllegalArgumentException(
                                "Colaborador no encontrado."
                        )
                );
    }


    /*
     * =====================================================
     * COMPARAR PROCESADORES
     * =====================================================
     */
    private boolean cumpleProcesador(
            String procesadorActivo,
            String procesadorMinimo) {


        /*
         * Si se exige procesador,
         * el activo debe tenerlo registrado.
         */
        if (procesadorActivo == null
                || procesadorActivo.isBlank()) {

            return false;
        }

        if (procesadorMinimo == null
                || procesadorMinimo.isBlank()) {

            return true;
        }

        int nivelActivo
                = obtenerNivelProcesador(
                        procesadorActivo
                );

        int nivelMinimo
                = obtenerNivelProcesador(
                        procesadorMinimo
                );


        /*
         * Si algún texto no puede clasificarse,
         * no descartamos el activo solamente
         * por el nombre del procesador.
         */
        if (nivelActivo == 0
                || nivelMinimo == 0) {

            return true;
        }

        return nivelActivo
                >= nivelMinimo;
    }


    /*
     * =====================================================
     * NIVEL DEL PROCESADOR
     * =====================================================
     */
    private int obtenerNivelProcesador(
            String procesador) {

        if (procesador == null) {

            return 0;
        }

        String cpu
                = procesador
                        .toLowerCase()
                        .replace(" ", "")
                        .replace("-", "");


        /*
         * Intel Core Ultra.
         */
        if (cpu.contains("ultra9")) {

            return 4;
        }

        if (cpu.contains("ultra7")) {

            return 3;
        }

        if (cpu.contains("ultra5")) {

            return 2;
        }


        /*
         * Intel Core tradicional / Ryzen.
         */
        if (cpu.contains("i9")
                || cpu.contains("ryzen9")) {

            return 4;
        }

        if (cpu.contains("i7")
                || cpu.contains("ryzen7")) {

            return 3;
        }

        if (cpu.contains("i5")
                || cpu.contains("ryzen5")) {

            return 2;
        }

        if (cpu.contains("i3")
                || cpu.contains("ryzen3")) {

            return 1;
        }

        return 0;
    }
}
