package com.consultorio.oftalmologico.domain.repository;

import com.consultorio.oftalmologico.domain.entities.historiaclinica.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, Long> {

}
