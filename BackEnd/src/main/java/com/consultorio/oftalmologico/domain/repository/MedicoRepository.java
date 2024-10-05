package com.consultorio.oftalmologico.domain.repository;

import com.consultorio.oftalmologico.domain.entities.medico.Medico;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    Medico findByEmail(String email);

    @Query("""
            SELECT m FROM Medico m
            WHERE m.id =:id
            AND m.activo != false
            """)
    Medico findByIdAndActivoFalse(Long id);

}
