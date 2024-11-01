package com.consultorio.oftalmologico.domain.repository;

import com.consultorio.oftalmologico.domain.entities.usuario.DetallesUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetallesUsuarioRepository extends JpaRepository<DetallesUsuario, Long> {

    DetallesUsuario findByUsuarioId(Long id);
}
