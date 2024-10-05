package com.consultorio.oftalmologico.domain.dto.paciente;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record DtoModificaPaciente(
        String apellido,
        String nombre,
        String telefono,
        Long dni,
        String ObraSocial,
        String numeroObraSocial
    ) {
}
