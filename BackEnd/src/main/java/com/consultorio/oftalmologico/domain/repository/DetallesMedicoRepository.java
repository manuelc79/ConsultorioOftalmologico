package com.consultorio.oftalmologico.domain.repository;

import com.consultorio.oftalmologico.domain.entities.medico.DetallesMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetallesMedicoRepository extends JpaRepository<DetallesMedico, Long> {

    DetallesMedico findByMedicoId(Long id);
}
