/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unimaq.sistemaactivos.service;

import com.unimaq.sistemaactivos.model.Activo;
import com.unimaq.sistemaactivos.model.EstadoActivo;
import com.unimaq.sistemaactivos.model.HistorialActivo;
import com.unimaq.sistemaactivos.model.UsuarioSistema;
import com.unimaq.sistemaactivos.repository.HistorialActivoRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class HistorialActivoService {

    private final HistorialActivoRepository historialActivoRepository;

    public HistorialActivoService(
            HistorialActivoRepository historialActivoRepository) {

        this.historialActivoRepository
                = historialActivoRepository;
    }

    public void registrarCambio(
            Activo activo,
            EstadoActivo estadoAnterior,
            EstadoActivo estadoNuevo,
            String comentario) {

        HistorialActivo historial
                = new HistorialActivo();

        historial.setActivo(activo);

        historial.setEstadoAnterior(
                estadoAnterior
        );

        historial.setEstadoNuevo(
                estadoNuevo
        );

        historial.setComentario(
                comentario
        );

        /*
         * Todavía no tenemos autenticación.
         * Por eso el usuario queda NULL.
         */
        historial.setUsuario(null);

        historialActivoRepository.save(
                historial
        );
    }

    public void registrarCambio(
            Activo activo,
            EstadoActivo estadoAnterior,
            EstadoActivo estadoNuevo,
            String comentario,
            UsuarioSistema usuario) {

        HistorialActivo historial
                = new HistorialActivo();

        historial.setActivo(activo);

        historial.setEstadoAnterior(
                estadoAnterior
        );

        historial.setEstadoNuevo(
                estadoNuevo
        );

        historial.setComentario(
                comentario
        );

        historial.setUsuario(
                usuario
        );

        historialActivoRepository.save(
                historial
        );
    }

    public List<HistorialActivo> obtenerHistorial(
            Integer idActivo) {

        return historialActivoRepository
                .findByActivoIdActivoOrderByFechaCambioDesc(
                        idActivo
                );
    }
}
