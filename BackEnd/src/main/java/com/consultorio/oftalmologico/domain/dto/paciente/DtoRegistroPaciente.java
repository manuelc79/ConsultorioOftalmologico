package com.consultorio.oftalmologico.domain.dto.paciente;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

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
