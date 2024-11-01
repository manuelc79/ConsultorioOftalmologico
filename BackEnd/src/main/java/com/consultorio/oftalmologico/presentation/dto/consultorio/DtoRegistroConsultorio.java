package com.consultorio.oftalmologico.presentation.dto.consultorio;

public record DtoRegistroConsultorio(
        String domicilio,
        String telefono,
        String localidad,
        String logo,
        Long usuarioId){
}
