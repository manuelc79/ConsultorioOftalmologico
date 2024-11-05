package com.consultorio.oftalmologico.presentation.dto.paciente;

import com.consultorio.oftalmologico.domain.entities.clinica.Clinica;
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
        Long clinicaId,
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
                        paciente.getClinica().getId(),
                        paciente.getActivo()
                );
        }
}
