package com.consultorio.oftalmologico.presentation.dto.medico;

import com.consultorio.oftalmologico.domain.entities.clinica.Clinica;
import com.consultorio.oftalmologico.domain.entities.consultorio.Consultorio;
import com.consultorio.oftalmologico.domain.enums.UserRole;

public record DtoModificaUsuario(
        Long id,
        String email,
        String password,
        String nombre,
        String apellido,
        String especialidad,
        Long numeroMatricula,
        Long telefono,
        UserRole role,
        Clinica clinica,
        Consultorio consultorio
) {
}
