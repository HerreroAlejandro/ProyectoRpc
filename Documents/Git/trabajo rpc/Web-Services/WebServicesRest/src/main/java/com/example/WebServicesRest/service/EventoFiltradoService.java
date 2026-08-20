package com.example.WebServicesRest.service;

import com.example.WebServicesRest.dto.EventoSolidarioDTO;
import com.example.WebServicesRest.model.EventoSolidario;
import com.example.WebServicesRest.model.Usuario;
import com.example.WebServicesRest.model.FiltroEvento;
import com.example.WebServicesRest.repository.EventoSolidarioRepository;
import com.example.WebServicesRest.repository.FiltroEventoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoFiltradoService {

    private final EventoSolidarioRepository eventoRepo;
    private final FiltroEventoRepository filtroRepo;

    public EventoFiltradoService(EventoSolidarioRepository eventoRepo, FiltroEventoRepository filtroRepo) {
        this.eventoRepo = eventoRepo;
        this.filtroRepo = filtroRepo;
    }

    public List<EventoSolidarioDTO> filtrarPorFiltro(Long filtroId) {
        FiltroEvento filtro = filtroRepo.findById(filtroId)
                .orElseThrow(() -> new RuntimeException("Filtro no encontrado"));

        List<EventoSolidario> eventos = eventoRepo.findAll();

        return eventos.stream()
                // Filtro por nombre de evento
                .filter(e -> filtro.getNombreEvento() == null ||
                        (e.getNombreEvento() != null &&
                                e.getNombreEvento().toLowerCase().contains(filtro.getNombreEvento().toLowerCase())))
                // Filtro por fecha desde
                .filter(e -> filtro.getFechaDesde() == null ||
                        (e.getFechaHoraEvento() != null && !e.getFechaHoraEvento().isBefore(filtro.getFechaDesde())))
                // Filtro por fecha hasta
                .filter(e -> filtro.getFechaHasta() == null ||
                        (e.getFechaHoraEvento() != null && !e.getFechaHoraEvento().isAfter(filtro.getFechaHasta())))
                // Filtro por participación
                .filter(e -> {
                    if (filtro.getParticipa() == null) return true;
                    if (e.getParticipantesEvento() == null) return !filtro.getParticipa();
                    boolean participa = e.getParticipantesEvento().stream()
                            .anyMatch(u -> u.getId().equals(filtro.getUsuarioId()));
                    return filtro.getParticipa().equals(participa);
                })

                // Mapeo a DTO
                .map(e -> {
                    EventoSolidarioDTO dto = new EventoSolidarioDTO();
                    dto.setId(e.getId());
                    dto.setNombreEvento(e.getNombreEvento());
                    dto.setDescripcion(e.getDescripcion());
                    dto.setFechaHoraEvento(e.getFechaHoraEvento());
                    dto.setParticipa(e.getParticipantesEvento() != null &&
                            e.getParticipantesEvento().stream()
                                    .anyMatch(u -> u.getId().equals(filtro.getUsuarioId())));
                    return dto;
                })
                .collect(Collectors.toList());
    }
}