package com.consultorio.oftalmologico.domain.repository;

import com.consultorio.oftalmologico.domain.entities.consultorio.Consultorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConsultorioRepository extends JpaRepository<Consultorio, Long> {

    Consultorio findByUsuarioId(Long id);

    @Query("""
            SELECT c FROM Consultorio c
            WHERE c.id = :id
            AND c.activo = true
            """)
    Consultorio buscarPorId(Long id);
}
