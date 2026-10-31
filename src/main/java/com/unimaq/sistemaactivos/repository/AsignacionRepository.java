package com.unimaq.sistemaactivos.repository;

import com.unimaq.sistemaactivos.model.Asignacion;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

public interface AsignacionRepository
        extends JpaRepository<Asignacion, Integer> {

    long countByEstadoAsignacion(
            String estadoAsignacion
    );

    @Query("""
        SELECT a
        FROM Asignacion a
        JOIN a.colaborador c
        JOIN a.activo ac
        LEFT JOIN ac.tipoEquipo te
        LEFT JOIN ac.sede s

        WHERE (
            :q IS NULL
            OR :q = ''
            OR LOWER(c.nombreCompleto)
                LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(c.codigoUsuario)
                LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(ac.numeroInventario)
                LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(ac.numeroSerie)
                LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(ac.modelo)
                LIKE LOWER(CONCAT('%', :q, '%'))
        )

        AND (
            :estado IS NULL
            OR :estado = ''
            OR a.estadoAsignacion = :estado
        )

        AND (
            :tipoId IS NULL
            OR te.idTipoEquipo = :tipoId
        )

        AND (
            :sedeId IS NULL
            OR s.idSede = :sedeId
        )
        """)
    Page<Asignacion> buscarConFiltros(
            @Param("q") String q,
            @Param("estado") String estado,
            @Param("tipoId") Integer tipoId,
            @Param("sedeId") Integer sedeId,
            Pageable pageable
    );
}
