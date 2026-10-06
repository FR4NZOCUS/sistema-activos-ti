/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unimaq.sistemaactivos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios_sistema")
public class UsuarioSistema {

    /*
     * =====================================================
     * ID
     * =====================================================
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;


    /*
     * =====================================================
     * DATOS DE ACCESO
     * =====================================================
     */
    @Column(
            name = "nombre_usuario",
            nullable = false
    )
    private String nombreUsuario;

    @Column(
            name = "password_hash",
            nullable = false
    )
    private String passwordHash;


    /*
     * =====================================================
     * DATOS PERSONALES
     * =====================================================
     */
    @Column(
            name = "nombre_completo",
            nullable = false
    )
    private String nombreCompleto;

    @Column(name = "correo")
    private String correo;


    /*
     * =====================================================
     * ROL
     * =====================================================
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "id_rol",
            nullable = false
    )
    private Rol rol;


    /*
     * =====================================================
     * ESTADO
     * =====================================================
     *
     * En MySQL:
     *
     * BIT / BIT(1)
     *
     * Java:
     *
     * true  = activo
     * false = inactivo
     */
    @Column(name = "estado")
    private Boolean estado;


    /*
     * =====================================================
     * FECHA DE CREACIÓN
     * =====================================================
     */
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;


    /*
     * =====================================================
     * CONSTRUCTOR
     * =====================================================
     */
    public UsuarioSistema() {
    }


    /*
     * =====================================================
     * GETTERS Y SETTERS
     * =====================================================
     */
    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
