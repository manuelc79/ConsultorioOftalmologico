package com.consultorio.oftalmologico.presentation.dto.paciente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DtoRegistroPaciente (
        @NotBlank(message = "El apellido es Obligatorio")
        String apellido,
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
        String telefono,
        @NotNull(message = "El DNI es obligatorio")
        Long dni,
        String ObraSocial,
        String numeroObraSocial,
        Boolean activo
){
}
