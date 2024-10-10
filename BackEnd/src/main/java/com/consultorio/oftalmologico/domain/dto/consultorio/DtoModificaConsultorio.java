package com.consultorio.oftalmologico.domain.dto.consultorio;

import jakarta.persistence.Lob;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

public record DtoModificaConsultorio(
        Long id,
        String domicilio,
        String telefono,
        String localidad,
        String logo,
        Long medicoId){
}
