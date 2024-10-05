package com.consultorio.oftalmologico.domain.dto.consulta;

import com.consultorio.oftalmologico.domain.entities.historiaclinica.HistoriaClinica;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record DtoRespuestaConsulta(
        Long id,
        LocalDate fechaConsulta,
        String agudezaVisualOISC,
        String agudezaVisualODSC,
        String agudezaVisualOICC,
        String agudezaVisualODCC,
        String lentesParaLejosOI,
        String lentesParaLejosOD,
        String lentesParaCercaAO,
        String observaciones,
        Long pacienteDni,
        Long medicoId ) {

        public DtoRespuestaConsulta(HistoriaClinica historiaClinica) {
                this(
                        historiaClinica.getId(),
                        historiaClinica.getFechaConsulta(),
                        historiaClinica.getAgudezaVisualOISC(),
                        historiaClinica.getAgudezaVisualODSC(),
                        historiaClinica.getAgudezaVisualOICC(),
                        historiaClinica.getAgudezaVisualODCC(),
                        historiaClinica.getLentesParaLejosOI(),
                        historiaClinica.getLentesParaLejosOD(),
                        historiaClinica.getLentesParaCercaAO(),
                        historiaClinica.getObservaciones(),
                        historiaClinica.getPacienteDni(),
                        historiaClinica.getMedicoId());
        }
}
