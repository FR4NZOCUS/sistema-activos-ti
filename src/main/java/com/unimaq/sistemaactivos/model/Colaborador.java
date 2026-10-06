package com.unimaq.sistemaactivos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "colaboradores")
public class Colaborador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_colaborador")
    private Integer idColaborador;

    @Column(
            name = "codigo_usuario",
            nullable = false,
            unique = true
    )
    private String codigoUsuario;

    @Column(name = "apellido_paterno")
    private String apellidoPaterno;

    @Column(name = "apellido_materno")
    private String apellidoMaterno;

    @Column(name = "nombres")
    private String nombres;

    @Column(
            name = "nombre_completo",
            nullable = false
    )
    private String nombreCompleto;

    @Column(name = "estado_colaborador")
    private String estadoColaborador;


    /*
     * =====================================================
     * PUESTO REAL
     * =====================================================
     *
     * Ejemplos:
     *
     * Representante de Ventas
     * Supervisor de Servicios
     * Jefe Administrativo
     * Planner de Mantenimiento
     *
     * Este valor proviene de la información real
     * de la organización.
     */
    @ManyToOne
    @JoinColumn(name = "id_puesto")
    private Puesto puesto;


    /*
     * =====================================================
     * PERFIL TECNOLÓGICO
     * =====================================================
     *
     * El campo Cargo se mantiene porque actualmente
     * el motor de asignación automática utiliza este
     * perfil para determinar los requisitos.
     *
     * Ejemplos:
     *
     * Gerente
     * Jefe
     * Diseñador
     * Técnico
     * Practicante Preprofesional
     * Practicante Profesional
     */
    @ManyToOne
    @JoinColumn(name = "id_cargo")
    private Cargo cargo;


    /*
     * =====================================================
     * CENTRO DE COSTO
     * =====================================================
     */
    @ManyToOne
    @JoinColumn(name = "id_centro_costo")
    private CentroCosto centroCosto;


    /*
     * =====================================================
     * SEDE
     * =====================================================
     */
    @ManyToOne
    @JoinColumn(name = "id_sede")
    private Sede sede;


    /*
     * =====================================================
     * UBICACIÓN
     * =====================================================
     */
    @ManyToOne
    @JoinColumn(name = "id_ubicacion")
    private UbicacionOperativa ubicacion;


    /*
     * =====================================================
     * CONSTRUCTOR
     * =====================================================
     */
    public Colaborador() {
    }


    /*
     * =====================================================
     * GETTERS Y SETTERS
     * =====================================================
     */
    public Integer getIdColaborador() {
        return idColaborador;
    }

    public void setIdColaborador(
            Integer idColaborador) {

        this.idColaborador = idColaborador;
    }

    public String getCodigoUsuario() {
        return codigoUsuario;
    }

    public void setCodigoUsuario(
            String codigoUsuario) {

        this.codigoUsuario = codigoUsuario;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(
            String apellidoPaterno) {

        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(
            String apellidoMaterno) {

        this.apellidoMaterno = apellidoMaterno;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(
            String nombres) {

        this.nombres = nombres;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(
            String nombreCompleto) {

        this.nombreCompleto = nombreCompleto;
    }

    public String getEstadoColaborador() {
        return estadoColaborador;
    }

    public void setEstadoColaborador(
            String estadoColaborador) {

        this.estadoColaborador = estadoColaborador;
    }

    public Puesto getPuesto() {
        return puesto;
    }

    public void setPuesto(
            Puesto puesto) {

        this.puesto = puesto;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(
            Cargo cargo) {

        this.cargo = cargo;
    }

    public CentroCosto getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(
            CentroCosto centroCosto) {

        this.centroCosto = centroCosto;
    }

    public Sede getSede() {
        return sede;
    }

    public void setSede(
            Sede sede) {

        this.sede = sede;
    }

    public UbicacionOperativa getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(
            UbicacionOperativa ubicacion) {

        this.ubicacion = ubicacion;
    }
}
