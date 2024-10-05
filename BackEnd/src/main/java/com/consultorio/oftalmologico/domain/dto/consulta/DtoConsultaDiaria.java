package com.consultorio.oftalmologico.domain.dto.consulta;

import com.consultorio.oftalmologico.domain.entities.historiaclinica.HistoriaClinica;
import com.consultorio.oftalmologico.domain.entities.paciente.Paciente;

import java.time.LocalDate;

public record DtoConsultaDiaria(
        Long id,
        LocalDate fechaConsulta,
        Long dni,
        String apellido,
        String nombre,
        String telefono,
        String ObraSocial) {

    public DtoConsultaDiaria(HistoriaClinica historiaClinica, Paciente paciente) {
        this(historiaClinica.getId(),
                historiaClinica.getFechaConsulta(),
                paciente.getDni(),
                paciente.getApellido(),
                paciente.getNombre(),
                paciente.getTelefono(),
                paciente.getObraSocial());
    }
}
