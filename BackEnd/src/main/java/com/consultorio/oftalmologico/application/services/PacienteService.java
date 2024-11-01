package com.consultorio.oftalmologico.application.services;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.consultorio.oftalmologico.domain.entities.paciente.Paciente;
import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;
import com.consultorio.oftalmologico.domain.enums.UserRole;
import com.consultorio.oftalmologico.domain.repository.PacienteRepository;
import com.consultorio.oftalmologico.domain.repository.UsuarioRepository;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.ObjectAlreadyExistsException;
import com.consultorio.oftalmologico.presentation.dto.paciente.DtoModificaPaciente;
import com.consultorio.oftalmologico.presentation.dto.paciente.DtoRegistroPaciente;
import com.consultorio.oftalmologico.presentation.dto.paciente.DtoRespuestaPaciente;

@Service
@Transactional(readOnly = true)
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    private void validarAccesoClinica(Usuario usuario, Long clinicaId) throws AccessDeniedException {
        if (usuario.getRole() != UserRole.ADMIN && !usuario.getClinica().getId().equals(clinicaId)) {
            throw new AccessDeniedException("No tiene acceso a esta clinica");
        }
    }

    public Long calcularEdad(LocalDate fechaNacimiento) {
        LocalDate fechaActual = LocalDate.now();
        Long edad = ChronoUnit.YEARS.between(fechaNacimiento, fechaActual);
        return edad;
    }

    @Cacheable(value = "pacienteCache", key = "#dni")
    public DtoRespuestaPaciente buscarPorDni(Long dni) {
        var paciente = pacienteRepository.findByDniAndActivo(dni);
        if (paciente == null) {
            throw new ObjectAlreadyExistsException("Paciente no encontrado");
        }
        return new DtoRespuestaPaciente(paciente);
    }

    @Transactional
    @CacheEvict(value = "pacienteCache", key = "#dato.dni")
    public DtoRespuestaPaciente crearPaciente(DtoRegistroPaciente dato) {
        if (pacienteRepository.findByDniAndActivo(dato.dni()) != null){
            throw new ObjectAlreadyExistsException("El DNI ya está en uso");
        }

        Paciente paciente = new Paciente();
        paciente.setApellido(dato.apellido());
        paciente.setNombre(dato.nombre());
        paciente.setTelefono(dato.telefono());
        paciente.setDni(dato.dni());
        paciente.setObraSocial(dato.ObraSocial());
        paciente.setNumeroObraSocial(dato.numeroObraSocial());
        paciente.setClinica(dato.clinica());
        paciente.setActivo(true);
        pacienteRepository.save(paciente);

        return new DtoRespuestaPaciente(paciente); //, calcularEdad(paciente.getFechaNacimiento()));
    }

    public Page<DtoRespuestaPaciente> listarPacientes(Pageable pageable, String userEmail) {
        var usuario = usuarioRepository.findByEmail(userEmail);
        var clinicaId = usuario.getClinica().getId();

        if (usuario.getRole() == UserRole.ADMIN) {
            return pacienteRepository.findAll(pageable)
                    .map(DtoRespuestaPaciente::new);
        }

        return pacienteRepository.findByClinicaIdOrderByApellido(clinicaId, pageable)
                .map(DtoRespuestaPaciente::new);

//        Page<Paciente> pacientes = pacienteRepository.findAllOrderByApellido(pageable);
//
//        List<DtoRespuestaPaciente> dtoList = pacientes.getContent().stream()
//                .map(DtoRespuestaPaciente::new)
//                .collect(Collectors.toList());
//        return new PageImpl<>(dtoList);
    }

    public Boolean eliminarPaciente(Long dni) {
        var paciente = pacienteRepository.findByDniAndActivo(dni);
        if (paciente == null) {
            return false;
        }
        paciente.setActivo(false);
        pacienteRepository.save(paciente);
        return true;
    }

    public DtoRespuestaPaciente modificarPaciente(DtoModificaPaciente dato) {
        var paciente = pacienteRepository.findByDniAndActivo(dato.dni());
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

    public List<DtoRespuestaPaciente> listarPacientesPorUsuario(Long usuarioId) {
        List<Paciente> pacientes = pacienteRepository.findByUsuariosIdAndActivoTrue(usuarioId);
        return pacientes.stream()
                .map(DtoRespuestaPaciente::new)
                .collect(Collectors.toList());
    }
}
