package com.unimaq.sistemaactivos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "puestos")
public class Puesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_puesto")
    private Integer idPuesto;

    @Column(
            name = "nombre_puesto",
            nullable = false,
            unique = true,
            length = 180
    )
    private String nombrePuesto;

    @Column(
            name = "descripcion",
            length = 255
    )
    private String descripcion;

    @Column(
            name = "estado",
            nullable = false
    )
    private Boolean estado;

    public Puesto() {
    }

    public Integer getIdPuesto() {
        return idPuesto;
    }

    public void setIdPuesto(
            Integer idPuesto) {

        this.idPuesto = idPuesto;
    }

    public String getNombrePuesto() {
        return nombrePuesto;
    }

    public void setNombrePuesto(
            String nombrePuesto) {

        this.nombrePuesto = nombrePuesto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(
            String descripcion) {

        this.descripcion = descripcion;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(
            Boolean estado) {

        this.estado = estado;
    }
}
