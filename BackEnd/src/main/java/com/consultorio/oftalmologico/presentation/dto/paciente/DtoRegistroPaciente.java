package com.consultorio.oftalmologico.presentation.dto.paciente;

import com.consultorio.oftalmologico.domain.entities.clinica.Clinica;
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
        Clinica clinica,
        Boolean activo
){
}
