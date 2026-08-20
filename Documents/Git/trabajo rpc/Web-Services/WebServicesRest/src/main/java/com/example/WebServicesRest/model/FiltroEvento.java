package com.example.WebServicesRest.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "filtro_eventos")
public class FiltroEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreFiltro;

    @Column(nullable = false)
    private Long usuarioId;

    private String nombreEvento;

    private LocalDateTime fechaDesde;
    private LocalDateTime fechaHasta;

    private Boolean participa;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreFiltro() {
        return nombreFiltro;
    }

    public void setNombreFiltro(String nombreFiltro) {
        this.nombreFiltro = nombreFiltro;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombreEvento() {
        return nombreEvento;
    }

    public void setNombreEvento(String nombreEvento) {
        this.nombreEvento = nombreEvento;
    }

    public LocalDateTime getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(LocalDateTime fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public LocalDateTime getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(LocalDateTime fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public Boolean getParticipa() {
        return participa;
    }

    public void setParticipa(Boolean participa) {
        this.participa = participa;
    }

    public FiltroEvento(Long id, String nombreFiltro, Long usuarioId, String nombreEvento, LocalDateTime fechaDesde, LocalDateTime fechaHasta, Boolean participa) {
        this.id = id;
        this.nombreFiltro = nombreFiltro;
        this.usuarioId = usuarioId;
        this.nombreEvento = nombreEvento;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.participa = participa;
    }

    public FiltroEvento(){}
}
