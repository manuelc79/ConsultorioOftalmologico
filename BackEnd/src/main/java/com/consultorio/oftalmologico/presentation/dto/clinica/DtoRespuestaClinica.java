package com.consultorio.oftalmologico.presentation.dto.clinica;

import com.consultorio.oftalmologico.domain.entities.clinica.Clinica;

public record DtoRespuestaClinica(
        Long id,
        String nombre,
        String domicilio,
        String identificacionFiscal,
        String pais,
        Boolean activo) {

    public DtoRespuestaClinica (Clinica clinica) {
        this(
                clinica.getId(),
                clinica.getNombre(),
                clinica.getDomicilio(),
                clinica.getIdentificacionFiscal(),
                clinica.getPais(),
                clinica.getActivo()
        );
    }
}
