package com.consultorio.oftalmologico.presentation.dto.clinica;

import jakarta.validation.constraints.NotBlank;

public record DtoRegistroClinica(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
        String domicilio,
        String identificacionFiscal,
        Boolean activo,
        @NotBlank(message = "El país es obligatorio")
        String pais
) {
}
