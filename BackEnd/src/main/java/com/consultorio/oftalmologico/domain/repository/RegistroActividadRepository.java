package com.consultorio.oftalmologico.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.consultorio.oftalmologico.domain.entities.RegistroActividad;

public interface RegistroActividadRepository extends JpaRepository<RegistroActividad, Long> {
}
