package com.example.WebServicesRest.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventario_donaciones")
public class InventarioDonaciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoria;
    private String descripcion;
    private int cantidad;
    private boolean eliminado;
    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraModificacion;

    @Column(name = "usuario_alta_id")
    private Long usuarioAltaId;

    @Column(name = "usuario_modificado_id")
    private Long usuarioModificadoId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public LocalDateTime getFechaHoraAlta() {
        return fechaHoraAlta;
    }

    public void setFechaHoraAlta(LocalDateTime fechaHoraAlta) {
        this.fechaHoraAlta = fechaHoraAlta;
    }

    public LocalDateTime getFechaHoraModificacion() {
        return fechaHoraModificacion;
    }

    public void setFechaHoraModificacion(LocalDateTime fechaHoraModificacion) {
        this.fechaHoraModificacion = fechaHoraModificacion;
    }

    public Long getUsuarioAltaId() {
        return usuarioAltaId;
    }

    public void setUsuarioAltaId(Long usuarioAltaId) {
        this.usuarioAltaId = usuarioAltaId;
    }

    public Long getUsuarioModificadoId() {
        return usuarioModificadoId;
    }

    public void setUsuarioModificadoId(Long usuarioModificadoId) {
        this.usuarioModificadoId = usuarioModificadoId;
    }

    public InventarioDonaciones(Long id, String categoria, String descripcion, int cantidad, boolean eliminado, LocalDateTime fechaHoraAlta, LocalDateTime fechaHoraModificacion, Long usuarioAltaId, Long usuarioModificadoId) {
        this.id = id;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.eliminado = eliminado;
        this.fechaHoraAlta = fechaHoraAlta;
        this.fechaHoraModificacion = fechaHoraModificacion;
        this.usuarioAltaId = usuarioAltaId;
        this.usuarioModificadoId = usuarioModificadoId;
    }

    public InventarioDonaciones(){}
}
