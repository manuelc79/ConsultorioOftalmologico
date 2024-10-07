package com.consultorio.oftalmologico.domain.dto.consultorio;

public record DtoModificaConsultorio(
        Long id,
        String domicilio,
        String telefono,
        String localidad,
        String logo,
        Long medicoId){
}
