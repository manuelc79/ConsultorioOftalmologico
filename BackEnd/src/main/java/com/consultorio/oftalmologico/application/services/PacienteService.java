package com.consultorio.oftalmologico.application.services;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.consultorio.oftalmologico.domain.entities.RegistroActividad;
import com.consultorio.oftalmologico.domain.entities.paciente.Paciente;
import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;
import com.consultorio.oftalmologico.domain.enums.UserRole;
import com.consultorio.oftalmologico.domain.repository.ClinicaRepository;
import com.consultorio.oftalmologico.domain.repository.PacienteRepository;
import com.consultorio.oftalmologico.domain.repository.RegistroActividadRepository;
import com.consultorio.oftalmologico.domain.repository.UsuarioRepository;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.EntidadNoEncontradaException;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.ObjectAlreadyExistsException;
import com.consultorio.oftalmologico.presentation.dto.registroActividad.DtoRegistroActividad;
import com.consultorio.oftalmologico.presentation.dto.paciente.DtoModificaPaciente;
import com.consultorio.oftalmologico.presentation.dto.paciente.DtoRegistroPaciente;
import com.consultorio.oftalmologico.presentation.dto.paciente.DtoRespuestaPaciente;

@Service
@Transactional(readOnly = true)
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    private final ClinicaRepository clinicaRepository;

    private final UsuarioRepository usuarioRepository;

    private final RegistroActividadService registroActividadService;

    public PacienteService(PacienteRepository pacienteRepository, ClinicaRepository clinicaRepository, UsuarioRepository usuarioRepository, RegistroActividadRepository registroActividadRepository, RegistroActividadService registroActividadService) {
        this.pacienteRepository = pacienteRepository;
        this.clinicaRepository = clinicaRepository;
        this.usuarioRepository = usuarioRepository;
        this.registroActividadService = registroActividadService;
    }

    private void validarAccesoClinica(Usuario usuario, Long clinicaId) throws AccessDeniedException {
        if (usuario.getRole() != UserRole.ADMIN && !usuario.getClinica().getId().equals(clinicaId)) {
            throw new AccessDeniedException("No tiene acceso a esta clínica");
        }
    }

    public Long calcularEdad(LocalDate fechaNacimiento) {
        LocalDate fechaActual = LocalDate.now();
        Long edad = ChronoUnit.YEARS.between(fechaNacimiento, fechaActual);
        return edad;
    }

    @Cacheable(value = "pacienteCache", key = "#dni")
    public DtoRespuestaPaciente buscarPorDni(Long dni, Authentication authentication) throws AccessDeniedException {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        var paciente = pacienteRepository.findByDniAndActivo(dni, usuarioLogueado.getClinica().getPais());
        
        if (paciente == null) {
            throw new ObjectAlreadyExistsException("Paciente no encontrado");
        }
        
        validarAccesoClinica(usuarioLogueado, paciente.getClinica().getId());
        
        return new DtoRespuestaPaciente(paciente);
    }

    @Transactional
    @CacheEvict(value = "pacienteCache", key = "#dato.dni")
    public DtoRespuestaPaciente crearPaciente(DtoRegistroPaciente dato, Authentication authentication) {
        var usarioLogueado = (Usuario) authentication.getPrincipal();
        if (clinicaRepository.findByIdAndTrue(usarioLogueado.getClinica().getId()) == null) {
            throw new EntidadNoEncontradaException("Clínica inexistente");
        }
        if (pacienteRepository.findByDniAndActivo(dato.dni(), usarioLogueado.getClinica().getPais()) != null){
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

        // Registrar la actividad usando la función optimizada
        registroActividadService.registroActividad(new DtoRegistroActividad(
                null, usarioLogueado.getId(), "CREAR",
                "Se registró un nuevo paciente: " + paciente.getDni()
        ));

        return new DtoRespuestaPaciente(paciente);
    }

    public Page<DtoRespuestaPaciente> listarPacientes(Pageable pageable, Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        var clinicaId = usuarioLogueado.getClinica().getId();

        return pacienteRepository.findByClinicaIdOrderByApellido(clinicaId, pageable)
                .map(DtoRespuestaPaciente::new);
    }

    public Boolean eliminarPaciente(Long dni, Authentication authentication) throws AccessDeniedException {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        var paciente = pacienteRepository.findByDniAndActivo(dni, usuarioLogueado.getClinica().getPais());
        
        if (paciente == null) {
            return false;
        }
        
        validarAccesoClinica(usuarioLogueado, paciente.getClinica().getId());
        
        paciente.setActivo(false);
        pacienteRepository.save(paciente);

        // Registrar la actividad
        registroActividadService.registroActividad(new DtoRegistroActividad(
                null, usuarioLogueado.getId(), "ELIMINAR",
                "Se eliminó el paciente: " + paciente.getDni()
        ));

        return true;
    }

    public DtoRespuestaPaciente modificarPaciente(DtoModificaPaciente dato, Authentication authentication) throws AccessDeniedException {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        var paciente = pacienteRepository.findByDniAndActivo(dato.dni(), usuarioLogueado.getClinica().getPais());
        
        if (paciente == null) {
            throw new ObjectAlreadyExistsException("Paciente no registrado");
        }
        
        validarAccesoClinica(usuarioLogueado, paciente.getClinica().getId());
        
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

        // Registrar la actividad
        registroActividadService.registroActividad(new DtoRegistroActividad(
                null, usuarioLogueado.getId(), "MODIFICAR",
                "Se modificó el paciente: " + paciente.getDni()
        ));
                
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

    public Boolean recuperarPaciente(Long dni, Authentication authentication) throws AccessDeniedException {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        var paciente = pacienteRepository.findByDniAndActivoFalse(dni, usuarioLogueado.getClinica().getPais());
        
        if (paciente == null) {
            return false;
        }
        
        validarAccesoClinica(usuarioLogueado, paciente.getClinica().getId());
        
        paciente.setActivo(true);
        pacienteRepository.save(paciente);

        // Registrar la actividad
        registroActividadService.registroActividad(new DtoRegistroActividad(
                null, usuarioLogueado.getId(), "RECUPERAR",
                "Se recuperó el paciente: " + paciente.getDni()
        ));

        return true;
    }
}
