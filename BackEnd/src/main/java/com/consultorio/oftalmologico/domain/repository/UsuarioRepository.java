package com.consultorio.oftalmologico.domain.repository;


import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;
import com.consultorio.oftalmologico.domain.enums.UserRole;
import com.consultorio.oftalmologico.presentation.dto.medico.DtoRespuestaUsuario;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
            WHERE u.id =:id
            """)
    DtoRespuestaUsuario findByUserId(Long id);

    @Query("""
            SELECT COUNT(u) > 0 FROM Usuario u
            WHERE u.role = :role
            """)
    boolean findByRole(UserRole role);

    @Query("""
            SELECT u FROM Usuario u
            WHERE u.id = :id
            AND u.clinica.id = :clinicaId
            AND u.activo = false
            """)
    Usuario findByIdAndActivoFalse(Long id, Long clinicaId);
}
