/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unimaq.sistemaactivos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "devoluciones")
public class Devolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_devolucion")
    private Integer idDevolucion;

    @ManyToOne
    @JoinColumn(name = "id_asignacion", nullable = false)
    private Asignacion asignacion;

    @Column(name = "fecha_devolucion")
    private LocalDateTime fechaDevolucion;

    @Column(name = "condicion")
    private String condicion;

    @Column(name = "observaciones")
    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "id_usuario_registra")
    private UsuarioSistema usuarioRegistra;

    public Devolucion() {
    }

    @PrePersist
    public void prePersist() {

        if (fechaDevolucion == null) {
            fechaDevolucion = LocalDateTime.now();
        }

        if (condicion == null || condicion.isBlank()) {
            condicion = "PENDIENTE_REVISION";
        }
    }

    public Integer getIdDevolucion() {
        return idDevolucion;
    }

    public void setIdDevolucion(Integer idDevolucion) {
        this.idDevolucion = idDevolucion;
    }

    public Asignacion getAsignacion() {
        return asignacion;
    }

    public void setAsignacion(Asignacion asignacion) {
        this.asignacion = asignacion;
    }

    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDateTime fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public UsuarioSistema getUsuarioRegistra() {
        return usuarioRegistra;
    }

    public void setUsuarioRegistra(UsuarioSistema usuarioRegistra) {
        this.usuarioRegistra = usuarioRegistra;
    }
}
