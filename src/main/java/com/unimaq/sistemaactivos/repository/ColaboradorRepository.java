package com.unimaq.sistemaactivos.repository;

import com.unimaq.sistemaactivos.model.Colaborador;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

public interface ColaboradorRepository
        extends JpaRepository<Colaborador, Integer> {


    /*
     * =====================================================
     * BÚSQUEDA CON FILTROS Y PAGINACIÓN
     * =====================================================
     *
     * Permite buscar por:
     *
     * - código de usuario;
     * - nombre completo;
     * - estado;
     * - cargo;
     * - sede.
     *
     * La paginación se ejecuta directamente
     * desde la base de datos.
     */
    @Query("""
        SELECT c
        FROM Colaborador c

        LEFT JOIN c.cargo cargo
        LEFT JOIN c.sede sede

        WHERE (

            :q IS NULL
            OR :q = ''

            OR LOWER(c.nombreCompleto)
                LIKE LOWER(CONCAT('%', :q, '%'))

            OR LOWER(c.codigoUsuario)
                LIKE LOWER(CONCAT('%', :q, '%'))
        )

        AND (

            :estado IS NULL
            OR :estado = ''
            OR c.estadoColaborador = :estado
        )

        AND (

            :cargoId IS NULL
            OR cargo.idCargo = :cargoId
        )

        AND (

            :sedeId IS NULL
            OR sede.idSede = :sedeId
        )
        """)
    Page<Colaborador> buscarConFiltros(
            @Param("q") String q,
            @Param("estado") String estado,
            @Param("cargoId") Integer cargoId,
            @Param("sedeId") Integer sedeId,
            Pageable pageable
    );
}
