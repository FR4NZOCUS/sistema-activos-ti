package com.unimaq.sistemaactivos.repository;

import com.unimaq.sistemaactivos.model.Activo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

public interface ActivoRepository
        extends JpaRepository<Activo, Integer> {


    /*
     * =====================================================
     * ACTIVOS POR ESTADO
     * =====================================================
     *
     * Utilizado por el módulo de recomendaciones.
     */
    List<Activo> findByEstadoNombreEstado(
            String nombreEstado
    );


    /*
     * =====================================================
     * CONTAR ACTIVOS POR ESTADO
     * =====================================================
     *
     * Utilizado por InicioController.
     */
    long countByEstadoNombreEstado(
            String nombreEstado
    );


    /*
     * =====================================================
     * GARANTÍAS PRÓXIMAS A VENCER
     * =====================================================
     *
     * Busca activos cuya garantía se encuentre
     * dentro del rango indicado.
     *
     * Los resultados se ordenan de la fecha
     * más cercana a la más lejana.
     *
     * Utilizado por InicioController.
     */
    List<Activo>
            findByFechaVencimientoGarantiaBetweenOrderByFechaVencimientoGarantiaAsc(
                    LocalDate fechaInicio,
                    LocalDate fechaFin
            );


    /*
     * =====================================================
     * INVENTARIO
     * FILTROS + PAGINACIÓN
     * =====================================================
     */
    @Query("""
        SELECT a
        FROM Activo a

        LEFT JOIN a.tipoEquipo tipo
        LEFT JOIN a.marca marca
        LEFT JOIN a.estado estado
        LEFT JOIN a.sede sede

        WHERE (

            :q IS NULL
            OR :q = ''

            OR LOWER(a.numeroInventario)
                LIKE LOWER(CONCAT('%', :q, '%'))

            OR LOWER(a.numeroSerie)
                LIKE LOWER(CONCAT('%', :q, '%'))

            OR LOWER(a.modelo)
                LIKE LOWER(CONCAT('%', :q, '%'))

            OR LOWER(a.procesador)
                LIKE LOWER(CONCAT('%', :q, '%'))
        )

        AND (

            :tipoId IS NULL
            OR tipo.idTipoEquipo = :tipoId
        )

        AND (

            :marcaId IS NULL
            OR marca.idMarca = :marcaId
        )

        AND (

            :estadoId IS NULL
            OR estado.idEstado = :estadoId
        )

        AND (

            :sedeId IS NULL
            OR sede.idSede = :sedeId
        )
        """)
    Page<Activo> buscarConFiltros(
            @Param("q") String q,
            @Param("tipoId") Integer tipoId,
            @Param("marcaId") Integer marcaId,
            @Param("estadoId") Integer estadoId,
            @Param("sedeId") Integer sedeId,
            Pageable pageable
    );
}
