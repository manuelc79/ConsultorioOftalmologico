package com.consultorio.oftalmologico.presentation.dto.consultorio;

import com.consultorio.oftalmologico.domain.entities.clinica.Clinica;
import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;
import jakarta.persistence.Lob;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

public record DtoModificaConsultorio(
                Long id,
                String domicilio,
                String telefono,
                String localidad,
                String logo,
                Usuario usuario,
                Clinica clinica) {
}
