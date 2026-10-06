package com.unimaq.sistemaactivos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "activos")
public class Activo {

    /*
     * =====================================================
     * ID
     * =====================================================
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_activo")
    private Integer idActivo;


    /*
     * =====================================================
     * IDENTIFICACIÓN DEL ACTIVO
     * =====================================================
     */
    @Column(
            name = "numero_inventario",
            nullable = false,
            unique = true
    )
    private String numeroInventario;

    @Column(
            name = "numero_serie",
            unique = true
    )
    private String numeroSerie;


    /*
     * =====================================================
     * TIPO DE EQUIPO
     * =====================================================
     */
    @ManyToOne
    @JoinColumn(name = "id_tipo_equipo")
    private TipoEquipo tipoEquipo;


    /*
     * =====================================================
     * MARCA
     * =====================================================
     */
    @ManyToOne
    @JoinColumn(name = "id_marca")
    private Marca marca;


    /*
     * =====================================================
     * MODELO
     * =====================================================
     */
    @Column(name = "modelo")
    private String modelo;


    /*
     * =====================================================
     * CARACTERÍSTICAS TÉCNICAS
     * =====================================================
     */
    @Column(name = "procesador")
    private String procesador;


    /*
     * Generación del procesador.
     *
     * Ejemplos:
     *
     * Intel Core i7 generación 9  -> 9
     * Intel Core i7 generación 11 -> 11
     * Intel Core i7 generación 14 -> 14
     *
     * Puede ser NULL mientras el equipo
     * todavía no haya sido revisado.
     */
    @Column(name = "generacion_procesador")
    private Integer generacionProcesador;

    @Column(name = "ram_gb")
    private Integer ramGb;

    @Column(name = "almacenamiento_gb")
    private Integer almacenamientoGb;


    /*
     * =====================================================
     * FECHAS
     * =====================================================
     */
    @Column(name = "fecha_compra")
    private LocalDate fechaCompra;

    @Column(name = "fecha_vencimiento_garantia")
    private LocalDate fechaVencimientoGarantia;


    /*
     * =====================================================
     * ESTADO
     * =====================================================
     */
    @ManyToOne
    @JoinColumn(name = "id_estado")
    private EstadoActivo estado;


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
     * UBICACIÓN OPERATIVA
     * =====================================================
     */
    @ManyToOne
    @JoinColumn(name = "id_ubicacion")
    private UbicacionOperativa ubicacion;


    /*
     * =====================================================
     * CENTRO DE COSTO
     * =====================================================
     *
     * Antes se manejaba únicamente como Integer:
     *
     * private Integer idCentroCosto;
     *
     * Ahora se modela correctamente como relación
     * con la tabla centros_costo.
     */
    @ManyToOne
    @JoinColumn(name = "id_centro_costo")
    private CentroCosto centroCosto;


    /*
     * =====================================================
     * OBSERVACIONES
     * =====================================================
     */
    @Column(name = "observaciones")
    private String observaciones;


    /*
     * =====================================================
     * CONSTRUCTOR
     * =====================================================
     */
    public Activo() {
    }


    /*
     * =====================================================
     * GETTERS Y SETTERS
     * =====================================================
     */
    public Integer getIdActivo() {
        return idActivo;
    }

    public void setIdActivo(Integer idActivo) {
        this.idActivo = idActivo;
    }

    public String getNumeroInventario() {
        return numeroInventario;
    }

    public void setNumeroInventario(
            String numeroInventario) {

        this.numeroInventario = numeroInventario;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(
            String numeroSerie) {

        this.numeroSerie = numeroSerie;
    }

    public TipoEquipo getTipoEquipo() {
        return tipoEquipo;
    }

    public void setTipoEquipo(
            TipoEquipo tipoEquipo) {

        this.tipoEquipo = tipoEquipo;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(
            Marca marca) {

        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(
            String modelo) {

        this.modelo = modelo;
    }

    public String getProcesador() {
        return procesador;
    }

    public void setProcesador(
            String procesador) {

        this.procesador = procesador;
    }

    public Integer getGeneracionProcesador() {
        return generacionProcesador;
    }

    public void setGeneracionProcesador(
            Integer generacionProcesador) {

        this.generacionProcesador
                = generacionProcesador;
    }

    public Integer getRamGb() {
        return ramGb;
    }

    public void setRamGb(
            Integer ramGb) {

        this.ramGb = ramGb;
    }

    public Integer getAlmacenamientoGb() {
        return almacenamientoGb;
    }

    public void setAlmacenamientoGb(
            Integer almacenamientoGb) {

        this.almacenamientoGb
                = almacenamientoGb;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(
            LocalDate fechaCompra) {

        this.fechaCompra = fechaCompra;
    }

    public LocalDate getFechaVencimientoGarantia() {
        return fechaVencimientoGarantia;
    }

    public void setFechaVencimientoGarantia(
            LocalDate fechaVencimientoGarantia) {

        this.fechaVencimientoGarantia
                = fechaVencimientoGarantia;
    }

    public EstadoActivo getEstado() {
        return estado;
    }

    public void setEstado(
            EstadoActivo estado) {

        this.estado = estado;
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

    public CentroCosto getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(
            CentroCosto centroCosto) {

        this.centroCosto = centroCosto;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(
            String observaciones) {

        this.observaciones = observaciones;
    }
}
