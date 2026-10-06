/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unimaq.sistemaactivos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "asignaciones")
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignacion")
    private Integer idAsignacion;

    @ManyToOne
    @JoinColumn(name = "id_colaborador", nullable = false)
    private Colaborador colaborador;

    @ManyToOne
    @JoinColumn(name = "id_activo", nullable = false)
    private Activo activo;

    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;

    @ManyToOne
    @JoinColumn(name = "id_usuario_registra")
    private UsuarioSistema usuarioRegistra;

    @Column(name = "estado_asignacion")
    private String estadoAsignacion;

    @Column(name = "observaciones")
    private String observaciones;

    public Asignacion() {
    }

    @PrePersist
    public void prePersist() {

        if (fechaAsignacion == null) {
            fechaAsignacion = LocalDateTime.now();
        }

        if (estadoAsignacion == null
                || estadoAsignacion.isBlank()) {

            estadoAsignacion = "ACTIVA";
        }
    }

    public Integer getIdAsignacion() {
        return idAsignacion;
    }

    public void setIdAsignacion(Integer idAsignacion) {
        this.idAsignacion = idAsignacion;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public Activo getActivo() {
        return activo;
    }

    public void setActivo(Activo activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public UsuarioSistema getUsuarioRegistra() {
        return usuarioRegistra;
    }

    public void setUsuarioRegistra(UsuarioSistema usuarioRegistra) {
        this.usuarioRegistra = usuarioRegistra;
    }

    public String getEstadoAsignacion() {
        return estadoAsignacion;
    }

    public void setEstadoAsignacion(String estadoAsignacion) {
        this.estadoAsignacion = estadoAsignacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
