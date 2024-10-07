package com.consultorio.oftalmologico.domain.dto.consultorio;

import com.consultorio.oftalmologico.domain.entities.medico.Consultorio;

public record DtoRespuestaConsultorio(
        Long id,
        String domicilio,
        String telefono,
        String localidad,
        String logo,
        Long medicoId){

    public DtoRespuestaConsultorio(Consultorio consultorio) {
        this(
                consultorio.getId(),
                consultorio.getDomicilio(),
                consultorio.getTelefono(),
                consultorio.getLocalidad(),
                consultorio.getLogo(),
                consultorio.getMedicoId()
        );
    }
}
