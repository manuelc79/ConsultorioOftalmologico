package com.consultorio.oftalmologico.presentation.dto.medico;

import com.consultorio.oftalmologico.domain.entities.usuario.DetallesUsuario;
import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;
import com.consultorio.oftalmologico.domain.enums.UserRole;

public record DtoRespuestaUsuario(
        Long id,
        String email,
        String nombre,
        String apellido,
        String especialidad,
        Long numeroMatricula,
        Long telefono,
        Boolean activo,
        UserRole role,
        Long clinicaId,
        Long consultorioId) {

    public DtoRespuestaUsuario(Usuario usuario, DetallesUsuario detallesUsuario) {
        this(
                usuario.getId(),
                usuario.getEmail(),
                detallesUsuario.getNombre(),
                detallesUsuario.getApellido(),
                detallesUsuario.getEspecialidad(),
                detallesUsuario.getNumeroMatricula(),
                detallesUsuario.getTelefono(),
                usuario.getActivo(),
                usuario.getRole(),
                usuario.getClinica() != null ? usuario.getClinica().getId() : null,
                usuario.getConsultorio() != null ? usuario.getConsultorio().getId() : null);
    }
}
