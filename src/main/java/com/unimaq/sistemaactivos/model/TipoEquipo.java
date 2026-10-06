/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unimaq.sistemaactivos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipos_equipo")
public class TipoEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_equipo")
    private Integer idTipoEquipo;

    @Column(name = "nombre_tipo", nullable = false, unique = true)
    private String nombreTipo;

    public TipoEquipo() {
    }

    public TipoEquipo(Integer idTipoEquipo, String nombreTipo) {
        this.idTipoEquipo = idTipoEquipo;
        this.nombreTipo = nombreTipo;
    }

    public Integer getIdTipoEquipo() {
        return idTipoEquipo;
    }

    public void setIdTipoEquipo(Integer idTipoEquipo) {
        this.idTipoEquipo = idTipoEquipo;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }
}
