package com.consultorio.oftalmologico.domain.dto.consulta;

import jakarta.validation.constraints.NotNull;


public record DtoNuevaConsulta(
        String agudezaVisualOISC,
        String agudezaVisualODSC,
        String agudezaVisualOICC,
        String agudezaVisualODCC,
        String lentesParaLejosOI,
        String lentesParaLejosOD,
        String lentesParaCercaAO,
        String observaciones,
        @NotNull(message = "El dni de paciente es obligatorio")
        Long pacienteDni,
        @NotNull(message = "El id del Medico es obligatorio")
        Long medicoId ) {
}
