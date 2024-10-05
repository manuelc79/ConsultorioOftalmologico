package com.consultorio.oftalmologico.domain.repository;

import com.consultorio.oftalmologico.domain.entities.historiaclinica.HistoriaClinica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface ConsultaRepository extends JpaRepository<HistoriaClinica, Long> {
    @Query("""
            SELECT c FROM HistoriaClinica c
            WHERE c.pacienteDni =:pacienteDni
            AND c.fechaConsulta =:fecha
            """)
    HistoriaClinica findByPacienteDniAndFechaConsulta(Long pacienteDni, LocalDate fecha);

    @Query("""
            SELECT c FROM HistoriaClinica c
            WHERE c.id =:id
            AND c.activo != false
            """)
    HistoriaClinica findByIdAndActivo(Long id);

    @Query("""
            SELECT c FROM HistoriaClinica c
            WHERE c.pacienteDni =:pacienteDni
            AND c.activo != false
            ORDER BY c.fechaConsulta DESC
            """)
    Page<HistoriaClinica> findByPacienteDniAndActivo(Long pacienteDni, Pageable pageable);

    @Query("""
            SELECT c FROM HistoriaClinica c
            WHERE c.fechaConsulta =:fecha
            AND c.medicoId =:medicoId
            AND c.activo != false
            """)
    Page<HistoriaClinica> findAllByFechaConsulta(LocalDate fecha, Long medicoId, Pageable pageable);
}
