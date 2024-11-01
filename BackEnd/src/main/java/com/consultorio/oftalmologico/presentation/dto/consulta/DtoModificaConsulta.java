package com.consultorio.oftalmologico.presentation.dto.consulta;

import java.time.LocalDate;

public record DtoModificaConsulta(
        Long id,
        LocalDate fechaConsulta,
        String agudezaVisualOISC,
        String agudezaVisualODSC,
        String agudezaVisualOICC,
        String agudezaVisualODCC,
        String lentesParaLejosOI,
        String lentesParaLejosOD,
        String lentesParaCercaAO,
        String observaciones) {
}
