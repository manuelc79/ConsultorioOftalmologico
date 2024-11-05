package com.consultorio.oftalmologico.domain.repository;

import com.consultorio.oftalmologico.domain.entities.paciente.Paciente;

import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    @Query(value = """
        SELECT p FROM Paciente p 
        WHERE p.dni = :dni 
        AND p.activo = true""")
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    Paciente findByDniAndActivo(Long dni);

    @Query(value = """
        SELECT p FROM Paciente p 
        WHERE p.clinica.id = :clinicaId 
        AND p.activo = true 
        ORDER BY p.apellido""")
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    Page<Paciente> findByClinicaIdOrderByApellido(Long clinicaId, Pageable pageable);

    @Query("""
            SELECT p FROM Paciente p
            JOIN p.usuarios u
            WHERE u.id = :usuarioId
            AND p.activo = true
            """)
    List<Paciente> findByUsuariosIdAndActivoTrue(Long usuarioId);

    @Query(value = """
        SELECT p FROM Paciente p 
        WHERE p.dni = :dni 
        AND p.activo = false""")
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    Paciente findByDniAndActivoFalse(Long dni);
}
