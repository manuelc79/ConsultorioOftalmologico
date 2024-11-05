package com.consultorio.oftalmologico.presentation.dto.consultorio;

import com.consultorio.oftalmologico.domain.entities.clinica.Clinica;
import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;
import org.springframework.lang.Nullable;

public record DtoRegistroConsultorio(
                String domicilio,
                String telefono,
                String localidad,
                String logo,

                @Nullable Usuario usuario,
                Clinica clinica) {
}
