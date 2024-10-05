package com.consultorio.oftalmologico.domain.dto.consultorio;

public record DtoModificaConsultorio(
        Long id,
        String domicilio,
        String piso,
        String oficina,
        String telefono,
        String logo,
        Long medicoId){
}
