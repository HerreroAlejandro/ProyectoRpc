package com.example.WebServicesRest.controller;

import com.example.WebServicesRest.dto.EventoSolidarioDTO;
import com.example.WebServicesRest.service.EventoFiltradoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoFiltradoController {

    private final EventoFiltradoService service;

    public EventoFiltradoController(EventoFiltradoService service) {
        this.service = service;
    }

    @GetMapping("/filtro/{filtroId}")
    public List<EventoSolidarioDTO> getEventosFiltrados(@PathVariable Long filtroId) {
        return service.filtrarPorFiltro(filtroId);
    }
}
