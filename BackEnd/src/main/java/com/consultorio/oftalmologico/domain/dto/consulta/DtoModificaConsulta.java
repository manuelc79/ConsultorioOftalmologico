package com.consultorio.oftalmologico.domain.dto.consulta;

import com.fasterxml.jackson.annotation.JsonFormat;
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
