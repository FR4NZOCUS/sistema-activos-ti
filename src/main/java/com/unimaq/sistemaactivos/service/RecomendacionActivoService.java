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

        this.activoRepository = activoRepository;
        this.colaboradorRepository = colaboradorRepository;
        this.requisitoCargoRepository = requisitoCargoRepository;
    }


    /*
     * =====================================================
     * EQUIPOS PRINCIPALES RECOMENDADOS
     * =====================================================
     */
    public List<Activo> obtenerRecomendados(
            Integer idColaborador) {

        Colaborador colaborador
                = buscarColaborador(idColaborador);

        if (colaborador.getCargo() == null) {

            return new ArrayList<>();
        }

        List<RequisitoCargo> requisitosPrincipales
                = requisitoCargoRepository
                        .findByCargoIdCargoAndTipoRequisito(
                                colaborador
                                        .getCargo()
                                        .getIdCargo(),
                                "PRINCIPAL"
                        );

        if (requisitosPrincipales == null
                || requisitosPrincipales.isEmpty()) {

            return new ArrayList<>();
        }

        List<Activo> disponibles
                = activoRepository
                        .findByEstadoNombreEstado(
                                "DISPONIBLE"
                        );

        List<Activo> recomendados
                = filtrarActivos(
                        disponibles,
                        requisitosPrincipales
                );

        ordenarActivosPrincipales(
                recomendados,
                requisitosPrincipales
        );

        return recomendados;
    }


    /*
     * =====================================================
     * SELECCIÓN AUTOMÁTICA DEL ACTIVO PRINCIPAL
     * =====================================================
     */
    public Activo obtenerActivoPrincipalAutomatico(
            Integer idColaborador) {

        List<Activo> recomendados
                = obtenerRecomendados(idColaborador);

        if (recomendados.isEmpty()) {

            return null;
        }

        return recomendados.get(0);
    }


    /*
     * =====================================================
     * REQUISITOS COMPLEMENTARIOS
     * =====================================================
     */
    public List<RequisitoCargo> obtenerComplementarios(
            Integer idColaborador) {

        Colaborador colaborador
                = buscarColaborador(idColaborador);

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
     * ACTIVOS COMPLEMENTARIOS DISPONIBLES
     * =====================================================
     */
    public List<Activo> obtenerActivosComplementarios(
            Integer idColaborador) {

        Colaborador colaborador
                = buscarColaborador(idColaborador);

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
                || requisitos == null
                || requisitos.isEmpty()) {

            return resultado;
        }

        for (Activo activo : activos) {

            for (RequisitoCargo requisito : requisitos) {

                if (cumpleRequisito(
                        activo,
                        requisito)) {

                    resultado.add(activo);

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

        if (activo == null
                || requisito == null) {

            return false;
        }


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
                    requisito.getProcesadorMinimo())) {

                return false;
            }
        }


        /*
         * =================================================
         * GENERACIÓN DEL PROCESADOR
         * =================================================
         */
        if (requisito.getGeneracionMinima() != null) {

            if (activo.getGeneracionProcesador() == null) {

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
        if (requisito.getRamMinimaGb() != null) {

            if (activo.getRamGb() == null
                    || activo.getRamGb()
                    < requisito.getRamMinimaGb()) {

                return false;
            }
        }


        /*
         * =================================================
         * ALMACENAMIENTO
         * =================================================
         */
        if (requisito
                .getAlmacenamientoMinimoGb()
                != null) {

            if (activo.getAlmacenamientoGb() == null
                    || activo.getAlmacenamientoGb()
                    < requisito
                            .getAlmacenamientoMinimoGb()) {

                return false;
            }
        }

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
                || activos.isEmpty()
                || requisitos == null
                || requisitos.isEmpty()) {

            return;
        }

        activos.sort(
                Comparator
                        /*
                         * MENOR EXCESO DE GENERACIÓN
                         */
                        .comparingInt(
                                (Activo activo)
                                -> mejorValorGeneracion(
                                        activo,
                                        requisitos
                                )
                        )
                        /*
                         * MENOR EXCESO DE PROCESADOR
                         */
                        .thenComparingInt(
                                (Activo activo)
                                -> mejorValorProcesador(
                                        activo,
                                        requisitos
                                )
                        )
                        /*
                         * MENOR EXCESO DE RAM
                         */
                        .thenComparingInt(
                                (Activo activo)
                                -> mejorValorRam(
                                        activo,
                                        requisitos
                                )
                        )
                        /*
                         * MENOR EXCESO DE ALMACENAMIENTO
                         */
                        .thenComparingInt(
                                (Activo activo)
                                -> mejorValorAlmacenamiento(
                                        activo,
                                        requisitos
                                )
                        )
                        /*
                         * CRITERIO FINAL
                         */
                        .thenComparingInt(
                                (Activo activo)
                                -> activo.getIdActivo() != null
                                ? activo.getIdActivo()
                                : Integer.MAX_VALUE
                        )
        );
    }


    /*
     * =====================================================
     * MEJOR VALOR DE GENERACIÓN
     * =====================================================
     */
    private int mejorValorGeneracion(
            Activo activo,
            List<RequisitoCargo> requisitos) {

        int mejor
                = Integer.MAX_VALUE;

        for (RequisitoCargo requisito : requisitos) {

            if (!cumpleRequisito(
                    activo,
                    requisito)) {

                continue;
            }

            int valor
                    = valorGeneracionOrden(
                            activo,
                            requisito
                    );

            if (valor < mejor) {

                mejor = valor;
            }
        }

        return mejor;
    }


    /*
     * =====================================================
     * MEJOR VALOR DE PROCESADOR
     * =====================================================
     */
    private int mejorValorProcesador(
            Activo activo,
            List<RequisitoCargo> requisitos) {

        int mejor
                = Integer.MAX_VALUE;

        for (RequisitoCargo requisito : requisitos) {

            if (!cumpleRequisito(
                    activo,
                    requisito)) {

                continue;
            }

            int valor
                    = valorProcesadorOrden(
                            activo,
                            requisito
                    );

            if (valor < mejor) {

                mejor = valor;
            }
        }

        return mejor;
    }


    /*
     * =====================================================
     * MEJOR VALOR DE RAM
     * =====================================================
     */
    private int mejorValorRam(
            Activo activo,
            List<RequisitoCargo> requisitos) {

        int mejor
                = Integer.MAX_VALUE;

        for (RequisitoCargo requisito : requisitos) {

            if (!cumpleRequisito(
                    activo,
                    requisito)) {

                continue;
            }

            int valor
                    = valorRamOrden(
                            activo,
                            requisito
                    );

            if (valor < mejor) {

                mejor = valor;
            }
        }

        return mejor;
    }


    /*
     * =====================================================
     * MEJOR VALOR DE ALMACENAMIENTO
     * =====================================================
     */
    private int mejorValorAlmacenamiento(
            Activo activo,
            List<RequisitoCargo> requisitos) {

        int mejor
                = Integer.MAX_VALUE;

        for (RequisitoCargo requisito : requisitos) {

            if (!cumpleRequisito(
                    activo,
                    requisito)) {

                continue;
            }

            int valor
                    = valorAlmacenamientoOrden(
                            activo,
                            requisito
                    );

            if (valor < mejor) {

                mejor = valor;
            }
        }

        return mejor;
    }


    /*
     * =====================================================
     * VALOR GENERACIÓN
     * =====================================================
     */
    private int valorGeneracionOrden(
            Activo activo,
            RequisitoCargo requisito) {

        if (activo.getGeneracionProcesador() == null) {

            return Integer.MAX_VALUE;
        }

        if (requisito.getGeneracionMinima() == null) {

            return 0;
        }

        return activo.getGeneracionProcesador()
                - requisito.getGeneracionMinima();
    }


    /*
     * =====================================================
     * VALOR PROCESADOR
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

            return 0;
        }

        return nivelActivo
                - nivelRequisito;
    }


    /*
     * =====================================================
     * VALOR RAM
     * =====================================================
     */
    private int valorRamOrden(
            Activo activo,
            RequisitoCargo requisito) {

        if (requisito.getRamMinimaGb() == null) {

            return 0;
        }

        if (activo.getRamGb() == null) {

            return Integer.MAX_VALUE;
        }

        return activo.getRamGb()
                - requisito.getRamMinimaGb();
    }


    /*
     * =====================================================
     * VALOR ALMACENAMIENTO
     * =====================================================
     */
    private int valorAlmacenamientoOrden(
            Activo activo,
            RequisitoCargo requisito) {

        if (requisito
                .getAlmacenamientoMinimoGb()
                == null) {

            return 0;
        }

        if (activo.getAlmacenamientoGb() == null) {

            return Integer.MAX_VALUE;
        }

        return activo.getAlmacenamientoGb()
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
                .findById(idColaborador)
                .orElseThrow(()
                        -> new IllegalArgumentException(
                                "Colaborador no encontrado."
                        )
                );
    }


    /*
     * =====================================================
     * COMPARACIÓN DE PROCESADORES
     * =====================================================
     */
    private boolean cumpleProcesador(
            String procesadorActivo,
            String procesadorMinimo) {


        /*
         * No hay requisito de procesador.
         */
        if (procesadorMinimo == null
                || procesadorMinimo.isBlank()) {

            return true;
        }


        /*
         * Existe requisito.
         * Por ello el activo debe tener procesador.
         */
        if (procesadorActivo == null
                || procesadorActivo.isBlank()) {

            return false;
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
         * Si alguno no puede reconocerse,
         * no se asigna automáticamente.
         */
        if (nivelActivo == 0
                || nivelMinimo == 0) {

            return false;
        }

        return nivelActivo
                >= nivelMinimo;
    }


    /*
     * =====================================================
     * NIVEL SIMPLIFICADO DE PROCESADOR
     * =====================================================
     *
     * Nivel 4 = i9 / Ryzen 9
     * Nivel 3 = i7 / Ryzen 7
     * Nivel 2 = i5 / Ryzen 5
     * Nivel 1 = i3 / Ryzen 3
     */
    private int obtenerNivelProcesador(
            String procesador) {

        if (procesador == null
                || procesador.isBlank()) {

            return 0;
        }

        String cpu
                = procesador
                        .toLowerCase()
                        .replace(" ", "")
                        .replace("-", "");

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
