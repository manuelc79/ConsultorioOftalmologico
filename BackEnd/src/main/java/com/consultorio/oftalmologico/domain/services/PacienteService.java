package com.consultorio.oftalmologico.domain.services;

import com.consultorio.oftalmologico.domain.dto.paciente.DtoModificaPaciente;
import com.consultorio.oftalmologico.domain.dto.paciente.DtoRegistroPaciente;
import com.consultorio.oftalmologico.domain.dto.paciente.DtoRespuestaPaciente;
import com.consultorio.oftalmologico.domain.entities.paciente.Paciente;
import com.consultorio.oftalmologico.domain.repository.PacienteRepository;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.ObjectAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    public Long calcularEdad(LocalDate fechaNacimiento) {
        LocalDate fechaActual = LocalDate.now();
        Long edad = ChronoUnit.YEARS.between(fechaNacimiento, fechaActual);
        return edad;
    }

    public DtoRespuestaPaciente crearPaciente (DtoRegistroPaciente dato) {
        if (pacienteRepository.findByDni(dato.dni()) != null){
            throw new ObjectAlreadyExistsException("El DNI ya está en uso");
        }

        Paciente paciente = new Paciente();
        paciente.setApellido(dato.apellido());
        paciente.setNombre(dato.nombre());
        paciente.setTelefono(dato.telefono());
        paciente.setDni(dato.dni());
        paciente.setObraSocial(dato.ObraSocial());
        paciente.setNumeroObraSocial(dato.numeroObraSocial());
        paciente.setActivo(true);
        pacienteRepository.save(paciente);

        return new DtoRespuestaPaciente(paciente); //, calcularEdad(paciente.getFechaNacimiento()));
    }

    public Page<DtoRespuestaPaciente> listarPacientes(Pageable pageable) {
        Page<Paciente> pacientes = pacienteRepository.findAllOrderByApellido(pageable);

        List<DtoRespuestaPaciente> dtoList = pacientes.getContent().stream()
                .map(DtoRespuestaPaciente::new)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList);
    }

    public DtoRespuestaPaciente buscarPorDni(Long dni) {
        var paciente = pacienteRepository.findByDniAndActivoNotFalse(dni);
        if (paciente == null) {
            throw new ObjectAlreadyExistsException("Paciente no registrado");
        }
        return new DtoRespuestaPaciente(paciente);
    }

    public Boolean eliminarPaciente(Long dni) {
        var paciente = pacienteRepository.findByDniAndActivoNotFalse(dni);
        if (paciente == null) {
            return false;
        }
        paciente.setActivo(false);
        pacienteRepository.save(paciente);
        return true;
    }

    public DtoRespuestaPaciente modificarPaciente(DtoModificaPaciente dato) {
        var paciente = pacienteRepository.findByDniAndActivoNotFalse(dato.dni());
        if (paciente == null) {
            throw new ObjectAlreadyExistsException("Paciente no registrado");
        }
        if (dato.apellido() != null) {
            paciente.setApellido(dato.apellido());
        }
        if (dato.nombre() != null) {
            paciente.setNombre(dato.nombre());
        }
        if (dato.telefono() != null) {
            paciente.setTelefono(dato.telefono());
        }
        if (dato.ObraSocial() != null) {
            paciente.setObraSocial(dato.ObraSocial());
        }
        if (dato.numeroObraSocial() != null) {
            paciente.setNumeroObraSocial(dato.numeroObraSocial());
        }
        pacienteRepository.save(paciente);
        return new DtoRespuestaPaciente(paciente);
    }
}
