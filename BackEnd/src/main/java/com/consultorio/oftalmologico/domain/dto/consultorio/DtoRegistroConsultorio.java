package com.consultorio.oftalmologico.domain.dto.consultorio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DtoRegistroConsultorio(
        @NotBlank(message = "El domicilio es obligatorio")
        String domicilio,
        String piso,
        String oficina,
        String telefono,
        String logo,
        @NotNull(message = "El dato del medico es obligatorio")
        Long medicoId){
}
