package com.example.WebServicesRest.service;

import com.example.WebServicesRest.dto.FiltroEventoDTO;
import com.example.WebServicesRest.model.FiltroEvento;
import com.example.WebServicesRest.repository.FiltroEventoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FiltroEventoService {

    private final FiltroEventoRepository repositorio;

    public FiltroEventoService(FiltroEventoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public FiltroEventoDTO guardarFiltro(FiltroEventoDTO dto) {
        FiltroEvento filtro = new FiltroEvento();
        filtro.setNombreFiltro(dto.getNombreFiltro());
        filtro.setUsuarioId(dto.getUsuarioId());
        filtro.setNombreEvento(dto.getNombreEvento());
        filtro.setFechaDesde(dto.getFechaDesde());
        filtro.setFechaHasta(dto.getFechaHasta());
        filtro.setParticipa(dto.getParticipa());

        FiltroEvento guardado = repositorio.save(filtro);
        dto.setId(guardado.getId());
        return dto;
    }

    public List<FiltroEventoDTO> listarFiltros(Long usuarioId) {
        return repositorio.findByUsuarioId(usuarioId)
                .stream()
                .map(f -> {
                    FiltroEventoDTO dto = new FiltroEventoDTO();
                    dto.setId(f.getId());
                    dto.setNombreFiltro(f.getNombreFiltro());
                    dto.setUsuarioId(f.getUsuarioId());
                    dto.setNombreEvento(f.getNombreEvento());
                    dto.setFechaDesde(f.getFechaDesde());
                    dto.setFechaHasta(f.getFechaHasta());
                    dto.setParticipa(f.getParticipa());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public FiltroEventoDTO actualizarFiltro(Long id, FiltroEventoDTO dto) {
        FiltroEvento filtro = repositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Filtro no encontrado"));

        filtro.setNombreFiltro(dto.getNombreFiltro());
        filtro.setNombreEvento(dto.getNombreEvento());
        filtro.setFechaDesde(dto.getFechaDesde());
        filtro.setFechaHasta(dto.getFechaHasta());
        filtro.setParticipa(dto.getParticipa());

        FiltroEvento actualizado = repositorio.save(filtro);
        dto.setId(actualizado.getId());
        return dto;
    }

    public void eliminarFiltro(Long id) {
        repositorio.deleteById(id);
    }
}