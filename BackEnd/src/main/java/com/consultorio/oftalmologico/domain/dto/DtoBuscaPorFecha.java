package com.consultorio.oftalmologico.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record DtoBuscaPorFecha(
        LocalDate fechaConsulta,
        Long medicoId) {
}
