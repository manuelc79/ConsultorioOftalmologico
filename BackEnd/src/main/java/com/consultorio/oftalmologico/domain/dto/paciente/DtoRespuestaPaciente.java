package com.consultorio.oftalmologico.domain.dto.paciente;

import com.consultorio.oftalmologico.domain.entities.paciente.Paciente;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record DtoRespuestaPaciente(
        Long id,
        String apellido,
        String nombre,
        String telefono,
        Long dni,
        String ObraSocial,
        String numeroObraSocial,
        Boolean activo
) {
        public DtoRespuestaPaciente(Paciente paciente) {
                this(
                        paciente.getId(),
                        paciente.getApellido(),
                        paciente.getNombre(),
                        paciente.getTelefono(),
                        paciente.getDni(),
                        paciente.getObraSocial(),
                        paciente.getNumeroObraSocial(),
                        paciente.getActivo()
                );
        }
}
