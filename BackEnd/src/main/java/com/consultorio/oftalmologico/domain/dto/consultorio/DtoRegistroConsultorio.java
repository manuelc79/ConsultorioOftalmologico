package com.consultorio.oftalmologico.domain.dto.consultorio;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

public record DtoRegistroConsultorio(
        String domicilio,
        String telefono,
        String localidad,
        String logo,
        Long medicoId){
}
