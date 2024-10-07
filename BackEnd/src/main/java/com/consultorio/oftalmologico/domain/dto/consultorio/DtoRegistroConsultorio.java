package com.consultorio.oftalmologico.domain.dto.consultorio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DtoRegistroConsultorio(
        String domicilio,
        String telefono,
        String localidad,
        String logo,
        Long medicoId){
}
