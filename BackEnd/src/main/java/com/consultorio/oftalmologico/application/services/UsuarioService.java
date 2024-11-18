package com.consultorio.oftalmologico.application.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.consultorio.oftalmologico.domain.entities.RegistroActividad;
import com.consultorio.oftalmologico.presentation.dto.registroActividad.DtoRegistroActividad;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.consultorio.oftalmologico.domain.entities.clinica.Clinica;
import com.consultorio.oftalmologico.domain.entities.usuario.DetallesUsuario;
import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;
import com.consultorio.oftalmologico.domain.enums.UserRole;
import com.consultorio.oftalmologico.domain.repository.DetallesUsuarioRepository;
import com.consultorio.oftalmologico.domain.repository.RegistroActividadRepository;
import com.consultorio.oftalmologico.domain.repository.UsuarioRepository;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.EntidadNoEncontradaException;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.ObjectAlreadyExistsException;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.RelacionNoValidaException;
import com.consultorio.oftalmologico.infraestructure.validations.ValidationUsuario;
import com.consultorio.oftalmologico.presentation.dto.medico.DtoModificaUsuario;
import com.consultorio.oftalmologico.presentation.dto.medico.DtoRegistroUsuario;
import com.consultorio.oftalmologico.presentation.dto.medico.DtoRespuestaUsuario;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final DetallesUsuarioRepository detallesUsuarioRepository;

    private final PasswordEncoder passwordEncoder;

    private final RegistroActividadService registroActividadService;


    public UsuarioService(UsuarioRepository usuarioRepository, DetallesUsuarioRepository detallesUsuarioRepository, PasswordEncoder passwordEncoder, RegistroActividadRepository registroActividadRepository, RegistroActividadService registroActividadService, RegistroActividadService registroActividad) {
        this.usuarioRepository = usuarioRepository;
        this.detallesUsuarioRepository = detallesUsuarioRepository;
        this.passwordEncoder = passwordEncoder;

        this.registroActividadService = registroActividadService;
    }

    public DtoRespuestaUsuario registrarUsuario(DtoRegistroUsuario dato, Authentication authentication) {
        validarAccesoAdmin(authentication);
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        String errores = ValidationUsuario.validarCamposEnBlanco(dato);
        if (!errores.equals("{}")) {
            throw new IllegalArgumentException("Campos inválidos" + errores);
        }
        verificarEmailExistente(dato.email());
        verificarRolAdminUnico(dato.role());
        verificarClinicaValida(dato.clinica());

        Usuario usuario = crearUsuario(dato);
        DetallesUsuario detallesUsuario = crearDetallesUsuario(dato, usuario);
        
        // Registrar la actividad
        registroActividadService.registroActividad(new DtoRegistroActividad(
                null, usuarioLogueado.getId(),
                "CREAR", "Se registró un nuevo usuario: " + usuario.getEmail()
        ));

        return new DtoRespuestaUsuario(usuario, detallesUsuario);
    }

    private void validarAccesoAdmin(Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        if (usuarioLogueado.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("No tiene permiso para realizar esta acción.");
        }
    }

    private void verificarEmailExistente(String email) {
        if (usuarioRepository.findByEmail(email) != null) {
            throw new ObjectAlreadyExistsException("El email ya está en uso");
        }
    }

    private void verificarRolAdminUnico(UserRole role) {
        if (role == UserRole.ADMIN && usuarioRepository.findByRole(role)) {
            throw new ObjectAlreadyExistsException("¡Ya existe un usuario ADMIN, no se admiten más administradores!");
        }
    }

    private void verificarClinicaValida(Clinica clinica) {
        if (clinica == null || clinica.getId() == null) {
            throw new RelacionNoValidaException("Debe especificar una clínica válida");
        }
    }

    private Usuario crearUsuario(DtoRegistroUsuario dato) {
        Usuario usuario = new Usuario();
        usuario.setEmail(dato.email());
        usuario.setPassword(passwordEncoder.encode(dato.password()));
        usuario.setActivo(true);
        usuario.setRole(dato.role());
        usuario.setClinica(dato.clinica());
        usuario.setConsultorio(dato.consultorio());
        return usuarioRepository.save(usuario);
    }

    private DetallesUsuario crearDetallesUsuario(DtoRegistroUsuario dato, Usuario usuario) {
        DetallesUsuario detallesUsuario = new DetallesUsuario();
        detallesUsuario.setApellido(dato.apellido());
        detallesUsuario.setNombre(dato.nombre());
        detallesUsuario.setEspecialidad(dato.especialidad());
        detallesUsuario.setNumeroMatricula(dato.numeroMatricula());
        detallesUsuario.setTelefono(dato.telefono());
        detallesUsuario.setUsuario(usuario);
        return detallesUsuarioRepository.save(detallesUsuario);
    }

    public DtoRespuestaUsuario buscarUsuario(Long id, Authentication authentication) {
        var usuarioLogeado = (Usuario) authentication.getPrincipal();
        Usuario usuario;
        
        if (usuarioLogeado.getRole() == UserRole.ADMIN) {
            usuario = usuarioRepository.findByUserId(id);
        } else {
            usuario = usuarioRepository.findByIdAndActivo(id, usuarioLogeado.getClinica().getId());
        }
        
        if (usuario == null) {
            throw new EntidadNoEncontradaException("Usuario no encontrado");
        }
        
        var detallesUsuario = detallesUsuarioRepository.findByUsuarioId(id);
        return new DtoRespuestaUsuario(usuario, detallesUsuario);
    }

    public DtoRespuestaUsuario modificaUsuario(DtoModificaUsuario dato, Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        if (usuarioLogueado.getRole() == UserRole.MEDICO && !Objects.equals(usuarioLogueado.getId(), dato.id())) {
            throw new AccessDeniedException("No tiene permiso para realizar esta acción.");
        }
        Usuario usuario = obtenerUsuario(dato, usuarioLogueado);

        if (usuario == null) {
            throw new EntidadNoEncontradaException("Usuario no encontrado");
        }

        // Verificar si se está intentando cambiar el rol a ADMIN
        if (dato.role() == UserRole.ADMIN && usuarioRepository.findByRole(UserRole.ADMIN)) {
            throw new ObjectAlreadyExistsException("¡Ya existe un usuario ADMIN, no se admiten más administradores!");
        }

        DetallesUsuario detallesUsuario = detallesUsuarioRepository.findByUsuarioId(usuario.getId());
        actualizarUsuario(dato, usuario, usuarioLogueado);
        actualizarDetallesUsuario(dato, detallesUsuario);

        usuarioRepository.save(usuario);
        detallesUsuarioRepository.save(detallesUsuario);

        // Registrar la actividad
        registroActividadService.registroActividad(new DtoRegistroActividad(
                null, usuarioLogueado.getId(), "MODIFICAR",
                "Se modificó el usuario: " + usuario.getEmail()
        ));

        return new DtoRespuestaUsuario(usuario, detallesUsuario);
    }

    private Usuario obtenerUsuario(DtoModificaUsuario dato, Usuario usuarioLogeado) {
        if (usuarioLogeado.getRole() == UserRole.ADMIN) {
            return usuarioRepository.findByUserId(dato.id());
        } else {
            return usuarioRepository.findByIdAndActivo(dato.id(), dato.clinica().getId());
        }
    }

    private void actualizarUsuario(DtoModificaUsuario dato, Usuario usuario, Usuario usuarioLogeado) {
        if (dato.password() != null && !dato.password().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dato.password()));
        }
        if (dato.role() != null && usuarioLogeado.getRole() == UserRole.ADMIN) {
            usuario.setRole(dato.role());
        }
        if (dato.clinica() != null && usuarioLogeado.getRole() == UserRole.ADMIN) {
            usuario.setClinica(dato.clinica());
        }
        if (dato.consultorio() != null && usuarioLogeado.getRole() == UserRole.ADMIN) {
            usuario.setConsultorio(dato.consultorio());
        }
    }

    private void actualizarDetallesUsuario(DtoModificaUsuario dato, DetallesUsuario detallesUsuario) {
        if (dato.nombre() != null) {
            detallesUsuario.setNombre(dato.nombre());
        }
        if (dato.apellido() != null) {
            detallesUsuario.setApellido(dato.apellido());
        }
        if (dato.especialidad() != null) {
            detallesUsuario.setEspecialidad(dato.especialidad());
        }
        if (dato.numeroMatricula() != null) {
            detallesUsuario.setNumeroMatricula(dato.numeroMatricula());
        }
        if (dato.telefono() != null) {
            detallesUsuario.setTelefono(dato.telefono());
        }
    }

    public Boolean eliminarUsuario(Long id, Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        if (usuarioLogueado.getRole() != UserRole.ADMIN ) {
            throw new AccessDeniedException("No tiene permiso para realizar esta acción.");
        }
        var usuario = usuarioRepository.findByUserId(id);
        if (usuario == null) {
            return false;
        }
        usuario.setActivo(false);
        usuarioRepository.save(usuario);

        // Registrar la actividad
        registroActividadService.registroActividad(new DtoRegistroActividad(
                null, usuarioLogueado.getId(), "ELIMINAR",
                "Se eliminó el usuario: " + usuario.getEmail()
        ));

        return true;
    }

    public Boolean restauraUsuario(Long id, Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        if (usuarioLogueado.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("No tiene permiso para realizar esta acción.");
        }
        var usuario = usuarioRepository.findByIdAndActivoFalse(id);
        if (usuario == null) {
            return false;
        }
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        // Registrar la actividad
        registroActividadService.registroActividad(new DtoRegistroActividad(
                null, usuarioLogueado.getId(), "RESTAURAR",
                "Se restauro el usuario: " + usuario.getEmail()
        ));

        return true;

    }

    public List<DtoRespuestaUsuario> listarUsuarios(Authentication authentication) {
        var usuarioLogeado = (Usuario) authentication.getPrincipal();
        if (usuarioLogeado.getRole() == UserRole.ADMIN) {
            // Si es ADMIN, listar todos los usuarios
            return usuarioRepository.findAllAndNotAdmin().stream()
                    .map(usuario -> new DtoRespuestaUsuario(usuario, detallesUsuarioRepository.findByUsuarioId(usuario.getId())))
                    .collect(Collectors.toList());
        } else if (usuarioLogeado.getRole() == UserRole.DIRECTOR) {
            // Si es DIRECTOR, listar usuarios de su clínica
            return usuarioRepository.findByClinicaId(usuarioLogeado.getClinica().getId()).stream()
                    .map(usuario -> new DtoRespuestaUsuario(usuario, detallesUsuarioRepository.findByUsuarioId(usuario.getId())))
                    .collect(Collectors.toList());
        } else {
            throw new AccessDeniedException("No tiene permiso para ver la lista de usuarios.");
        }
    }
}
