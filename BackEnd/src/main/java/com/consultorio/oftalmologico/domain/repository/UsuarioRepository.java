package com.consultorio.oftalmologico.domain.repository;


import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;
import com.consultorio.oftalmologico.domain.enums.UserRole;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByEmail(String email);

    @Query("""
            SELECT u FROM Usuario u
            WHERE u.id =:id
            AND u.clinica.id = :clinicaId
            AND u.activo != false
            """)
    Usuario findByIdAndActivo(Long id, Long clinicaId);


    @Query("""
            SELECT u FROM Usuario u
            WHERE u.id = :id
            """)
    Usuario findByUserId(Long id);

    @Query("""
            SELECT COUNT(u) > 0 FROM Usuario u
            WHERE u.role = :role
            """)
    boolean findByRole(UserRole role);

    @Query("""
            SELECT u FROM Usuario u
            WHERE u.id = :id
            AND u.activo = false
            """)
    Usuario findByIdAndActivoFalse(Long id);

    @Query("""
            SELECT u FROM Usuario u
            WHERE u.clinica.id = :clinicaId
            AND u.activo = true
            """)
    List<Usuario> findByClinicaId(Long clinicaId);

    @Query("""
            SELECT u FROM Usuario u
            WHERE u.role != ADMIN
            ORDER BY u.clinica.id
            """)
    Collection<Usuario> findAllAndNotAdmin();
}
