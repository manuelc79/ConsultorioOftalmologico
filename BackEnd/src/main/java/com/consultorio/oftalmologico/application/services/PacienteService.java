package com.consultorio.oftalmologico.application.services;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.consultorio.oftalmologico.domain.repository.ClinicaRepository;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.EntidadNoEncontradaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
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
    ClinicaRepository clinicaRepository;

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
    public DtoRespuestaPaciente buscarPorDni(Long dni, Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        var paciente = pacienteRepository.findByDniAndActivo(dni);
        if (paciente == null || !Objects.equals(paciente.getClinica().getId(), usuarioLogueado.getClinica().getId())) {
            throw new ObjectAlreadyExistsException("Paciente no encontrado");
        }

        return new DtoRespuestaPaciente(paciente);
    }

    @Transactional
    @CacheEvict(value = "pacienteCache", key = "#dato.dni")
    public DtoRespuestaPaciente crearPaciente(DtoRegistroPaciente dato, Authentication authentication) {
        var usarioLogueado = (Usuario) authentication.getPrincipal();
        if (clinicaRepository.findByIdAndTrue(usarioLogueado.getClinica().getId()) == null) {
            throw new EntidadNoEncontradaException("Clínica inexistente");
        }
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
        paciente.setClinica(usarioLogueado.getClinica());
        paciente.setActivo(true);
        pacienteRepository.save(paciente);

        return new DtoRespuestaPaciente(paciente); //, calcularEdad(paciente.getFechaNacimiento()));
    }

    public Page<DtoRespuestaPaciente> listarPacientes(Pageable pageable, Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        var clinicaId = usuarioLogueado.getClinica().getId();

        return pacienteRepository.findByClinicaIdOrderByApellido(clinicaId, pageable)
                .map(DtoRespuestaPaciente::new);

//        Page<Paciente> pacientes = pacienteRepository.findAllOrderByApellido(pageable);
//
//        List<DtoRespuestaPaciente> dtoList = pacientes.getContent().stream()
//                .map(DtoRespuestaPaciente::new)
//                .collect(Collectors.toList());
//        return new PageImpl<>(dtoList);
    }

    public Boolean eliminarPaciente(Long dni, Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        var paciente = pacienteRepository.findByDniAndActivo(dni);
        if (paciente == null || !Objects.equals(usuarioLogueado.getClinica().getId(), paciente.getClinica().getId())) {
            return false;
        }
        paciente.setActivo(false);
        pacienteRepository.save(paciente);
        return true;
    }

    public DtoRespuestaPaciente modificarPaciente(DtoModificaPaciente dato, Authentication authentication) {
        var usuaroLogueado = (Usuario) authentication.getPrincipal();
        var paciente = pacienteRepository.findByDniAndActivo(dato.dni());
        if (paciente == null || !Objects.equals(usuaroLogueado.getClinica().getId(), paciente.getClinica().getId())) {
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

    public List<DtoRespuestaPaciente> listarPacientesPorUsuario(Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        List<Paciente> pacientes = pacienteRepository.findByUsuariosIdAndActivoTrue(usuarioLogueado.getId());
        return pacientes.stream()
                .map(DtoRespuestaPaciente::new)
                .collect(Collectors.toList());
    }

    public Boolean recuperarPaceinte(Long dni, Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        var paciente = pacienteRepository.findByDniAndActivoFalse(dni);
        if (paciente == null || !Objects.equals(usuarioLogueado.getClinica().getId(), paciente.getClinica().getId())) {
            return false;
        }
        paciente.setActivo(true);
        pacienteRepository.save(paciente);
        return true;
    }
}
