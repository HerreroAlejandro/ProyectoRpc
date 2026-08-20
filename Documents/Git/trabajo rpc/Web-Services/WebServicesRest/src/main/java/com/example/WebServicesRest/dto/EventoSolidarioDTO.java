package com.example.WebServicesRest.dto;

import java.time.LocalDateTime;

public class EventoSolidarioDTO {

    private Long id;
    private String nombreEvento;
    private String descripcion;
    private LocalDateTime fechaHoraEvento;
    private Boolean participa;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreEvento() {
        return nombreEvento;
    }

    public void setNombreEvento(String nombreEvento) {
        this.nombreEvento = nombreEvento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaHoraEvento() {
        return fechaHoraEvento;
    }

    public void setFechaHoraEvento(LocalDateTime fechaHoraEvento) {
        this.fechaHoraEvento = fechaHoraEvento;
    }

    public Boolean getParticipa() {
        return participa;
    }

    public void setParticipa(Boolean participa) {
        this.participa = participa;
    }

    public EventoSolidarioDTO(Long id, String nombreEvento, String descripcion, LocalDateTime fechaHoraEvento, Boolean participa) {
        this.id = id;
        this.nombreEvento = nombreEvento;
        this.descripcion = descripcion;
        this.fechaHoraEvento = fechaHoraEvento;
        this.participa = participa;
    }

    public EventoSolidarioDTO(){}
}
