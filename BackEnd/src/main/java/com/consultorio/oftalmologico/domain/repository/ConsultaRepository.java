package com.consultorio.oftalmologico.domain.repository;

import java.time.LocalDate;

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
        WHERE h.pacienteDni = :pacienteDni 
        AND h.fechaConsulta = :fecha 
        AND h.activo = true""")
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
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
            WHERE c.fechaConsulta = :fecha
            AND c.usuario.id = :usuarioId
            AND c.activo != false
            """)
    Page<HistoriaClinica> findAllByFechaConsulta(LocalDate fecha, Long usuarioId, Pageable pageable);
}
