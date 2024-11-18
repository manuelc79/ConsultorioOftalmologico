package com.consultorio.oftalmologico.application.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.consultorio.oftalmologico.domain.entities.RegistroActividad;
import com.consultorio.oftalmologico.domain.repository.RegistroActividadRepository;
import com.consultorio.oftalmologico.presentation.dto.registroActividad.DtoRegistroActividad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.consultorio.oftalmologico.domain.entities.consultorio.Consultorio;
import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;
import com.consultorio.oftalmologico.domain.enums.UserRole;
import com.consultorio.oftalmologico.domain.repository.ClinicaRepository;
import com.consultorio.oftalmologico.domain.repository.ConsultorioRepository;
import com.consultorio.oftalmologico.domain.repository.UsuarioRepository;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.EntidadNoEncontradaException;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.ObjectAlreadyExistsException;
import com.consultorio.oftalmologico.presentation.dto.consultorio.DtoModificaConsultorio;
import com.consultorio.oftalmologico.presentation.dto.consultorio.DtoRegistroConsultorio;
import com.consultorio.oftalmologico.presentation.dto.consultorio.DtoRespuestaConsultorio;

@Service
public class ConsultorioService {

    private final ConsultorioRepository consultorioRepository;

    private final UsuarioRepository usuarioRepository;

    private final ClinicaRepository clinicaRepository;

    private final RegistroActividadService registroActividadService;

    public ConsultorioService(ConsultorioRepository consultorioRepository, UsuarioRepository usuarioRepository, ClinicaRepository clinicaRepository, RegistroActividadRepository registroActividadRepository, RegistroActividadService registroActividadService) {
        this.consultorioRepository = consultorioRepository;
        this.usuarioRepository = usuarioRepository;
        this.clinicaRepository = clinicaRepository;
        this.registroActividadService = registroActividadService;
    }

    public DtoRespuestaConsultorio crearConsultorio(DtoRegistroConsultorio dato, Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        if (usuarioLogueado.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("No tiene permiso para realizar esta acción.");
        }

        if (dato.usuario() != null) {
            var usuario = usuarioRepository.findByIdAndActivo(dato.usuario().getId(), dato.clinica().getId());
            if (usuario == null) {
                throw new EntidadNoEncontradaException("Medico inexistente");
            }
            if (consultorioRepository.findByUsuarioId(dato.usuario().getId()) != null) {
                throw new ObjectAlreadyExistsException("Este medico ya tiene asignado un consultorio");
            }
        }

        if (dato.clinica() == null || clinicaRepository.findByIdAndTrue(dato.clinica().getId()) == null) {
            throw new EntidadNoEncontradaException("Clínica inexistente, debe enviar un valor de clínica válido");
        }

        Consultorio nuevoConsultorio = new Consultorio();
        nuevoConsultorio.setDomicilio(dato.domicilio());
        nuevoConsultorio.setTelefono(dato.telefono());
        nuevoConsultorio.setLocalidad(dato.localidad());
        nuevoConsultorio.setLogo(dato.logo());
        nuevoConsultorio.setActivo(true);
        nuevoConsultorio.setUsuario(dato.usuario());
        nuevoConsultorio.setClinica(dato.clinica());

        consultorioRepository.save(nuevoConsultorio);

        // Registrar la actividad
        registroActividadService.registroActividad(new DtoRegistroActividad(
                null, usuarioLogueado.getId(), "CREAR",
                "Se registró un nuevo consultorio " + nuevoConsultorio.getId()
        ));

        return new DtoRespuestaConsultorio(nuevoConsultorio);
    }

    public DtoRespuestaConsultorio buscarConsultorio(Long id, Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        Consultorio consultorio;
        if (usuarioLogueado.getRole() == UserRole.ADMIN) {
            consultorio = consultorioRepository.findByUsuarioId(id);
        } else if (usuarioLogueado.getRole() == UserRole.MEDICO && usuarioLogueado.getId() != id) {
            throw new AccessDeniedException("No tiene permiso para realizar esta acción.");
        } else {
            consultorio = consultorioRepository.findByUsuarioIdAndClinicaId(id, usuarioLogueado.getClinica().getId());
        }

        if (consultorio == null) {
            throw new EntidadNoEncontradaException("Este usuario aún no tiene asignado un consultorio");
        }
        return new DtoRespuestaConsultorio(consultorio);
    }

    public DtoRespuestaConsultorio modificaConsultorio(DtoModificaConsultorio dato, Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        Consultorio consultorio = obtenerConsultorio(dato, usuarioLogueado);
        
        if (consultorio == null) {
            throw new EntidadNoEncontradaException("Consultorio inexistente");
        }
        
        actualizarConsultorio(dato, consultorio, usuarioLogueado);
        consultorioRepository.save(consultorio);

        // Registrar la actividad
        registroActividadService.registroActividad(new DtoRegistroActividad(
                null, usuarioLogueado.getId(), "MODIFICAR",
                "Se modificó consultorio " + consultorio.getId()
        ));

        return new DtoRespuestaConsultorio(consultorio);
    }

    private Consultorio obtenerConsultorio(DtoModificaConsultorio dato, Usuario usuarioLogueado) {
        if (usuarioLogueado.getRole() == UserRole.ADMIN) {
            return consultorioRepository.buscarPorId(dato.id());
        } else if (usuarioLogueado.getRole() == UserRole.MEDICO && usuarioLogueado.getConsultorio().getId() != dato.id()) {
            throw new AccessDeniedException("No tiene permiso para realizar esta acción.");
        } else {
            return consultorioRepository.findByIdAndClinicaId(dato.id(), usuarioLogueado.getClinica().getId());
        }
    }

    private void actualizarConsultorio(DtoModificaConsultorio dato, Consultorio consultorio, Usuario usuarioLogueado) {
        if (dato.domicilio() != null) {
            consultorio.setDomicilio(dato.domicilio());
        }
        if (dato.telefono() != null) {
            consultorio.setTelefono(dato.telefono());
        }
        if (dato.localidad() != null) {
            consultorio.setLocalidad(dato.localidad());
        }
        if (dato.logo() != null) {
            consultorio.setLogo(dato.logo());
        }
        if (usuarioLogueado.getRole() == UserRole.ADMIN) {
            if (dato.usuario() != null && dato.usuario().getId() != null) {
                var usuario = usuarioRepository.findByIdAndActivo(dato.usuario().getId(), dato.clinica().getId());
                if (usuario == null) {
                    throw new EntidadNoEncontradaException("Médico inexistente");
                }
                consultorio.setUsuario(usuario);
            }
            if (dato.clinica() != null && dato.clinica().getId() != null) {
                var clinica = clinicaRepository.findById(dato.clinica().getId());
                if (clinica.isEmpty()) {
                    throw new EntidadNoEncontradaException("Clínica inexistente");
                }
                consultorio.setClinica(clinica.get());
            }
        }
    }

    public Boolean eliminarConsultorio(Long id, Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        if (usuarioLogueado.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("No tiene permiso para realizar esta acción.");
        }
        var consultorio = consultorioRepository.buscarPorId(id);
        if (consultorio == null) {
            return false;
        }
        consultorio.setActivo(false);
        consultorioRepository.save(consultorio);

        // Registrar la actividad
        registroActividadService.registroActividad(new DtoRegistroActividad(
                null, usuarioLogueado.getId(), "ELIMINAR",
                "Se eliminó el consultorio " + consultorio.getId()
        ));

        return true;
    }

    public Boolean restaurarConsultorio(Long id, Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        if (usuarioLogueado.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("No tiene permiso para realizar esta acción.");
        }
        var consultorio = consultorioRepository.findByIdAndActivoFalse(id);
        if (consultorio == null) {
            return false;
        }
        consultorio.setActivo(true);
        consultorioRepository.save(consultorio);

        // Registrar la actividad
        registroActividadService.registroActividad(new DtoRegistroActividad(
                null, usuarioLogueado.getId(), "RECUPERAR",
                "Se restauró el consultorio " + consultorio.getId()
        ));

        return true;
    }

    public Page<DtoRespuestaConsultorio> listarConsultrios(Pageable pageable, Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        Page<Consultorio> consultorio;

        if (usuarioLogueado.getRole() == UserRole.MEDICO) {
            throw new AccessDeniedException("No tiene permiso para realizar esta acción.");
        }
        if (usuarioLogueado.getRole() == UserRole.ADMIN) {
            consultorio = consultorioRepository.findAllOrderByClinica(pageable);
        } else {
            consultorio = consultorioRepository.findAllByClinica(pageable, usuarioLogueado.getClinica().getId());
        }

        if (consultorio.isEmpty()) {
            throw new EntidadNoEncontradaException("No se encontró consultorios para mostrar");
        }

        List<DtoRespuestaConsultorio> dtoList = consultorio.getContent().stream()
                .map(c -> new DtoRespuestaConsultorio(c))
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList);
    }
}
