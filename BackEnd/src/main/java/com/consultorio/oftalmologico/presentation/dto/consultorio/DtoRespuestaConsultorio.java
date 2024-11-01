package com.consultorio.oftalmologico.presentation.dto.consultorio;

import com.consultorio.oftalmologico.domain.entities.consultorio.Consultorio;

public record DtoRespuestaConsultorio(
        Long id,
        String domicilio,
        String telefono,
        String localidad,
        String logo,
        Long usuarioId) {

    public DtoRespuestaConsultorio(Consultorio consultorio) {
        this(
                consultorio.getId(),
                consultorio.getDomicilio(),
                consultorio.getTelefono(),
                consultorio.getLocalidad(),
                consultorio.getLogo(),
                consultorio.getUsuario().getId());
    }
}
