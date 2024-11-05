package com.consultorio.oftalmologico.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.consultorio.oftalmologico.domain.entities.clinica.Clinica;

@Repository
public interface ClinicaRepository extends JpaRepository<Clinica, Long> {

    @Query("""
            SELECT c FROM Clinica c
            WHERE c.id = :id
            AND c.activo != false
            """)
    Clinica findByIdAndTrue(Long id);

    Clinica findByNombre(String nombre);

    @Query("""
            SELECT c FROM Clinica c
            WHERE c.activo != false
            ORDER BY c.nombre
            """)
    Page<Clinica> findAllOrderByNombre(Pageable pageable);

    @Query("""
            SELECT c FROM Clinica c
            WHERE c.identificacionFiscal = :iFiscal
            """)
    Clinica findByIdentificacionFiscal(String iFiscal);


}
