package com.example.WebServicesRest.controller;

import com.example.WebServicesRest.dto.FiltroEventoDTO;
import com.example.WebServicesRest.service.FiltroEventoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filtros-eventos")
public class FiltroEventoController {

    private final FiltroEventoService filtroService;

    public FiltroEventoController(FiltroEventoService filtroService) {
        this.filtroService = filtroService;
    }

    // --- FILTROS ---
    @PostMapping
    public FiltroEventoDTO guardarFiltro(@RequestBody FiltroEventoDTO dto) {
        return filtroService.guardarFiltro(dto);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<FiltroEventoDTO> listarFiltros(@PathVariable Long usuarioId) {
        return filtroService.listarFiltros(usuarioId);
    }



    @DeleteMapping("/{id}")
    public void eliminarFiltro(@PathVariable Long id) {
        filtroService.eliminarFiltro(id);
    }

}
