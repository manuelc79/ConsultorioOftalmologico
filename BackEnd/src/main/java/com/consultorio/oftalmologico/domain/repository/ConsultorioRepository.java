package com.consultorio.oftalmologico.domain.repository;

import com.consultorio.oftalmologico.domain.entities.medico.Consultorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConsultorioRepository extends JpaRepository<Consultorio, Long> {

    Consultorio findByMedicoId(Long id);

    @Query("""
            SELECT c FROM Consultorio c
            WHERE c.id =:id
            """)
    Consultorio buscarPorId(Long id);
}
