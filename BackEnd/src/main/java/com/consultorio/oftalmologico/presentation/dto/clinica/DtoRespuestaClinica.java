package com.consultorio.oftalmologico.presentation.dto.clinica;

import com.consultorio.oftalmologico.domain.entities.clinica.Clinica;

public record DtoRespuestaClinica(
        Long id,
        String nombre,
        String domicilio,
        String informacionFiscal,
        Boolean activo) {

    public DtoRespuestaClinica (Clinica clinica) {
        this(
                clinica.getId(),
                clinica.getNombre(),
                clinica.getDomicilio(),
                clinica.getIdentificacionFiscal(),
                clinica.getActivo()
        );
    }
}
