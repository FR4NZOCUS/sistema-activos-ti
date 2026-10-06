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
@Table(name = "requisitos_cargo")
public class RequisitoCargo {

    /*
     * =====================================================
     * ID
     * =====================================================
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_requisito")
    private Integer idRequisito;


    /*
     * =====================================================
     * CARGO
     * =====================================================
     */
    @ManyToOne
    @JoinColumn(
            name = "id_cargo",
            nullable = false
    )
    private Cargo cargo;


    /*
     * =====================================================
     * TIPO DE EQUIPO
     * =====================================================
     */
    @ManyToOne
    @JoinColumn(
            name = "id_tipo_equipo",
            nullable = false
    )
    private TipoEquipo tipoEquipo;


    /*
     * =====================================================
     * PROCESADOR MÍNIMO
     * =====================================================
     */
    @Column(name = "procesador_minimo")
    private String procesadorMinimo;


    /*
     * =====================================================
     * GENERACIÓN MÍNIMA
     * =====================================================
     *
     * NUEVO.
     *
     * Ejemplo:
     *
     * Gerente
     * Core i7
     * Generación mínima = 12
     */
    @Column(name = "generacion_minima")
    private Integer generacionMinima;


    /*
     * =====================================================
     * RAM MÍNIMA
     * =====================================================
     */
    @Column(name = "ram_minima_gb")
    private Integer ramMinimaGb;


    /*
     * =====================================================
     * ALMACENAMIENTO MÍNIMO
     * =====================================================
     */
    @Column(name = "almacenamiento_minimo_gb")
    private Integer almacenamientoMinimoGb;


    /*
     * =====================================================
     * COMPLEMENTOS HISTÓRICOS
     * =====================================================
     *
     * Estos campos se mantienen porque ya forman
     * parte de la estructura actual.
     *
     * Para la nueva lógica de complementos estamos
     * utilizando también registros explícitos tipo
     * COMPLEMENTARIO.
     */
    @Column(name = "requiere_monitor")
    private Boolean requiereMonitor;

    @Column(name = "requiere_movil")
    private Boolean requiereMovil;


    /*
     * =====================================================
     * PRIORIDAD
     * =====================================================
     */
    @Column(name = "prioridad")
    private Integer prioridad;


    /*
     * =====================================================
     * OBSERVACIONES
     * =====================================================
     */
    @Column(name = "observaciones")
    private String observaciones;


    /*
     * =====================================================
     * TIPO DE REQUISITO
     * =====================================================
     *
     * Valores:
     *
     * PRINCIPAL
     * COMPLEMENTARIO
     */
    @Column(
            name = "tipo_requisito",
            nullable = false
    )
    private String tipoRequisito;


    /*
     * =====================================================
     * CONSTRUCTOR
     * =====================================================
     */
    public RequisitoCargo() {
    }


    /*
     * =====================================================
     * GETTERS Y SETTERS
     * =====================================================
     */
    public Integer getIdRequisito() {
        return idRequisito;
    }

    public void setIdRequisito(
            Integer idRequisito) {

        this.idRequisito
                = idRequisito;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(
            Cargo cargo) {

        this.cargo
                = cargo;
    }

    public TipoEquipo getTipoEquipo() {
        return tipoEquipo;
    }

    public void setTipoEquipo(
            TipoEquipo tipoEquipo) {

        this.tipoEquipo
                = tipoEquipo;
    }

    public String getProcesadorMinimo() {
        return procesadorMinimo;
    }

    public void setProcesadorMinimo(
            String procesadorMinimo) {

        this.procesadorMinimo
                = procesadorMinimo;
    }

    public Integer getGeneracionMinima() {
        return generacionMinima;
    }

    public void setGeneracionMinima(
            Integer generacionMinima) {

        this.generacionMinima
                = generacionMinima;
    }

    public Integer getRamMinimaGb() {
        return ramMinimaGb;
    }

    public void setRamMinimaGb(
            Integer ramMinimaGb) {

        this.ramMinimaGb
                = ramMinimaGb;
    }

    public Integer getAlmacenamientoMinimoGb() {
        return almacenamientoMinimoGb;
    }

    public void setAlmacenamientoMinimoGb(
            Integer almacenamientoMinimoGb) {

        this.almacenamientoMinimoGb
                = almacenamientoMinimoGb;
    }

    public Boolean getRequiereMonitor() {
        return requiereMonitor;
    }

    public void setRequiereMonitor(
            Boolean requiereMonitor) {

        this.requiereMonitor
                = requiereMonitor;
    }

    public Boolean getRequiereMovil() {
        return requiereMovil;
    }

    public void setRequiereMovil(
            Boolean requiereMovil) {

        this.requiereMovil
                = requiereMovil;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(
            Integer prioridad) {

        this.prioridad
                = prioridad;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(
            String observaciones) {

        this.observaciones
                = observaciones;
    }

    public String getTipoRequisito() {
        return tipoRequisito;
    }

    public void setTipoRequisito(
            String tipoRequisito) {

        this.tipoRequisito
                = tipoRequisito;
    }
}
