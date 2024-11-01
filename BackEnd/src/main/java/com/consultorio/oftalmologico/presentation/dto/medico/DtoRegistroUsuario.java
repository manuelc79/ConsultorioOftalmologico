package com.consultorio.oftalmologico.presentation.dto.medico;

import com.consultorio.oftalmologico.domain.entities.clinica.Clinica;
import com.consultorio.oftalmologico.domain.entities.consultorio.Consultorio;
import com.consultorio.oftalmologico.domain.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DtoRegistroUsuario(
        @NotBlank(message = "El correo no debe estar en blanco")
        String email,
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        @NotBlank(message = "El correo no debe estar en blanco")
        @NotNull
        String password,
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
        @NotBlank(message = "El apellido es obligatorio")
        String apellido,
        @NotBlank(message = "La especialidad es obligatoria")
        String especialidad,
        @NotNull(message = "La matricula es obligatoria")
        Long numeroMatricula,
        @NotNull(message = "El teléfono es obligatorio")
        Long telefono,
        @NotNull(message = "El rol es obligatorio")
        UserRole role,
        Clinica clinica,
        Consultorio consultorio
) {
}
