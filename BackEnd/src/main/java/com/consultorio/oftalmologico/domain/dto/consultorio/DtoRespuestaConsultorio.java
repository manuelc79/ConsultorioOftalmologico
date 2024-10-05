package com.consultorio.oftalmologico.domain.dto.consultorio;

import com.consultorio.oftalmologico.domain.entities.medico.Consultorio;

public record DtoRespuestaConsultorio(
        Long id,
        String domicilio,
        String piso,
        String oficina,
        String telefono,
        String logo,
        Long medicoId){

    public DtoRespuestaConsultorio(Consultorio consultorio) {
        this(
                consultorio.getId(),
                consultorio.getDomicilio(),
                consultorio.getPiso(),
                consultorio.getOficina(),
                consultorio.getTelefono(),
                consultorio.getLogo(),
                consultorio.getMedicoId()
        );
    }
}
