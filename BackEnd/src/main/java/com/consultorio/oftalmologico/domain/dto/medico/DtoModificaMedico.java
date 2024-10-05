package com.consultorio.oftalmologico.domain.dto.medico;

public record DtoModificaMedico(
        Long id,
        String email,
        String password,
        String nombre,
        String apellido,
        String especialidad,
        Long numeroMatricula,
        Long telefono
) {
}
