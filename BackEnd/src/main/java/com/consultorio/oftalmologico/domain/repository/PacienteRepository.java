package com.consultorio.oftalmologico.domain.repository;

import com.consultorio.oftalmologico.domain.entities.paciente.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Paciente findByDni (Long dni);

    @Query("""
            SELECT p FROM Paciente p
            WHERE p.activo != false
            ORDER BY p.apellido
            """)
    Page<Paciente> findAllOrderByApellido(Pageable pageable);

    @Query("""
            SELECT p FROM Paciente p
            WHERE p.dni = :dni 
            AND p.activo != false
            """)
    Paciente findByDniAndActivoNotFalse(Long dni);
}
