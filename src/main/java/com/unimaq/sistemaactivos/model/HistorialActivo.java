/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unimaq.sistemaactivos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_activo")
public class HistorialActivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Integer idHistorial;

    @ManyToOne
    @JoinColumn(name = "id_activo", nullable = false)
    private Activo activo;

    @ManyToOne
    @JoinColumn(name = "id_estado_anterior")
    private EstadoActivo estadoAnterior;

    @ManyToOne
    @JoinColumn(name = "id_estado_nuevo", nullable = false)
    private EstadoActivo estadoNuevo;

    @Column(name = "fecha_cambio")
    private LocalDateTime fechaCambio;

    @Column(name = "comentario")
    private String comentario;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private UsuarioSistema usuario;

    public HistorialActivo() {
    }

    @PrePersist
    public void prePersist() {

        if (fechaCambio == null) {
            fechaCambio = LocalDateTime.now();
        }
    }

    public Integer getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(Integer idHistorial) {
        this.idHistorial = idHistorial;
    }

    public Activo getActivo() {
        return activo;
    }

    public void setActivo(Activo activo) {
        this.activo = activo;
    }

    public EstadoActivo getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(EstadoActivo estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public EstadoActivo getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(EstadoActivo estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDateTime fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }
}