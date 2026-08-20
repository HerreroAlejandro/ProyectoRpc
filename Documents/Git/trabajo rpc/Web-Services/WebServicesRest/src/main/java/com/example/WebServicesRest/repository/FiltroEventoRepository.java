package com.example.WebServicesRest.repository;

import com.example.WebServicesRest.model.FiltroEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FiltroEventoRepository extends JpaRepository<FiltroEvento, Long> {
    List<FiltroEvento> findByUsuarioId(Long usuarioId);
}
