package com.example.WebServicesRest.repository;

import com.example.WebServicesRest.model.EventoSolidario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoSolidarioRepository extends JpaRepository<EventoSolidario, Long> {

}
