package com.consultorio.oftalmologico.presentation.dto.consultorio;

import com.consultorio.oftalmologico.domain.entities.clinica.Clinica;
import com.consultorio.oftalmologico.domain.entities.consultorio.Consultorio;
import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;

public record DtoRespuestaConsultorio(
        Long id,
        String domicilio,
        String telefono,
        String localidad,
        String logo,
        Boolean activo,
        Long usuarioId,
        Long clinicaId) {

    public DtoRespuestaConsultorio(Consultorio consultorio) {
        this(
                consultorio.getId(),
                consultorio.getDomicilio(),
                consultorio.getTelefono(),
                consultorio.getLocalidad(),
                consultorio.getLogo(),
                consultorio.getActivo(),
                consultorio.getUsuario() != null ? consultorio.getUsuario().getId() : null,
                consultorio.getClinica() != null ? consultorio.getClinica().getId() : null
        );
    }
}
