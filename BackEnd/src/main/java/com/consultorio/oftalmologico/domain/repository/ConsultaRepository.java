package com.consultorio.oftalmologico.domain.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import com.consultorio.oftalmologico.domain.entities.historiaclinica.HistoriaClinica;

import jakarta.persistence.QueryHint;

@Repository
public interface ConsultaRepository extends JpaRepository<HistoriaClinica, Long> {
    @Query(value = """
        SELECT h FROM HistoriaClinica h 
        WHERE h.paciente.dni = :pacienteDni 
        AND h.fechaConsulta = :fecha 
        AND h.activo = true""")
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    HistoriaClinica findByPacienteDniAndFechaConsulta(Long pacienteDni, LocalDate fecha);

    @Query("""
            SELECT c FROM HistoriaClinica c
            WHERE c.id = :id
            AND c.clinica.id = :clinicaId
            AND c.activo != false
            """)
    HistoriaClinica findByIdAndActivo(Long id, Long clinicaId);

    @Query("""
            SELECT c FROM HistoriaClinica c
            WHERE c.paciente.dni = :pacienteDni
            AND c.clinica.id = :clinicaId
            AND c.activo != false
            ORDER BY c.fechaConsulta DESC
            """)
    Page<HistoriaClinica> findByPacienteDniAndActivo(Long pacienteDni, Pageable pageable, Long clinicaId);

    @Query("""
            SELECT c FROM HistoriaClinica c
            WHERE c.fechaConsulta = :fecha
            AND c.usuario.id = :usuarioId
            AND c.clinica.id = :clinicaId
            AND c.activo != false
            """)
    Page<HistoriaClinica> findAllByFechaConsulta(LocalDate fecha, Long usuarioId, Pageable pageable, Long clinicaId);

    @Query("""
            SELECT c FROM HistoriaClinica c
            WHERE c.clinica.id = :clinicaId
            """)
    Page<HistoriaClinica> findAllByConsultorio(Pageable page, Long clinicaId);
}
