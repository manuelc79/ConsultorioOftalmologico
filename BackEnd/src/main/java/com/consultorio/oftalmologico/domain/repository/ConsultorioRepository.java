package com.consultorio.oftalmologico.domain.repository;

import com.consultorio.oftalmologico.domain.entities.consultorio.Consultorio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConsultorioRepository extends JpaRepository<Consultorio, Long> {

    @Query("""
            SELECT c FROM Consultorio c
            WHERE c.usuario.id = :id
            """)
    Consultorio findByUsuarioId(Long id);

    @Query("""
            SELECT c FROM Consultorio c
            WHERE c.id = :id
            AND c.activo = true
            """)
    Consultorio buscarPorId(Long id);

    @Query("""
            SELECT c FROM Consultorio c
               WHERE c.usuario.id = :id
               AND c.clinica.id = :clinicaId
               AND c.activo = true
            """)
    Consultorio findByUsuarioIdAndClinicaId(Long id, Long clinicaId);

    @Query("""
            SELECT c FROM  Consultorio c
            WHERE c.id = :id
            AND c.activo = false
            """)
    Consultorio findByIdAndActivoFalse(Long id);

    @Query("""
            SELECT c FROM Consultorio c
            ORDER BY c.clinica.id
            """)
    Page<Consultorio> findAllOrderByClinica(Pageable pageable);

    @Query("""
            SELECT c FROM Consultorio c
            WHERE c.clinica.id = :clinicaId
            """)
    Page<Consultorio> findAllByClinica(Pageable pageable, Long clinicaId);

    @Query("""
            SELECT c FROM Consultorio c
               WHERE c.id = :id
               AND c.clinica.id = :clinicaId
               AND c.activo = true
            """)
    Consultorio findByIdAndClinicaId(Long id, Long clinicaId);
}
