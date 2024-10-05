package com.consultorio.oftalmologico.domain.dto.medico;

import com.consultorio.oftalmologico.domain.entities.medico.DetallesMedico;
import com.consultorio.oftalmologico.domain.entities.medico.Medico;

public record DtoRespuestaMedico(
        Long id,
        String email,
        String nombre,
        String apellido,
        String especialidad,
        Long numeroMatricula,
        Long telefono,
        Boolean activo
) {
    public DtoRespuestaMedico(Medico medico, DetallesMedico detallesMedico) {
        this(
                medico.getId(),
                medico.getEmail(),
                detallesMedico.getNombre(),
                detallesMedico.getApellido(),
                detallesMedico.getEspecialidad(),
                detallesMedico.getNumeroMatricula(),
                detallesMedico.getTelefono(),
                medico.getActivo()
        );
    }
}
