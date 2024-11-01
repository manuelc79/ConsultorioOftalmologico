package com.consultorio.oftalmologico.presentation.dto.medico;

import jakarta.validation.constraints.NotBlank;

public record DtoAutenticarUsuario(
        @NotBlank
        String email,
        @NotBlank
        String password
) {
}
