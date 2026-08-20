package com.example.WebServicesRest.repository;

import com.example.WebServicesRest.model.InventarioDonaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventarioDonacionesRepository extends JpaRepository<InventarioDonaciones, Long> {
}
