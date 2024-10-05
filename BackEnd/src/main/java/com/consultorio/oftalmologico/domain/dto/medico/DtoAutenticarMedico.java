package com.consultorio.oftalmologico.domain.dto.medico;

import jakarta.validation.constraints.NotBlank;

public record DtoAutenticarMedico(
        @NotBlank
        String email,
        @NotBlank
        String password
) {
}
