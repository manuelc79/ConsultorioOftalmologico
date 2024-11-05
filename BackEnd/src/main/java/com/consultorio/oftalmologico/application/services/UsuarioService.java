package com.consultorio.oftalmologico.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.consultorio.oftalmologico.domain.entities.usuario.DetallesUsuario;
import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;
import com.consultorio.oftalmologico.domain.enums.UserRole;
import com.consultorio.oftalmologico.domain.repository.DetallesUsuarioRepository;
import com.consultorio.oftalmologico.domain.repository.UsuarioRepository;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.EntidadNoEncontradaException;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.ObjectAlreadyExistsException;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.RelacionNoValidaException;
import com.consultorio.oftalmologico.infraestructure.validations.ValidationUsuario;
import com.consultorio.oftalmologico.presentation.dto.medico.DtoModificaUsuario;
import com.consultorio.oftalmologico.presentation.dto.medico.DtoRegistroUsuario;
import com.consultorio.oftalmologico.presentation.dto.medico.DtoRespuestaUsuario;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DetallesUsuarioRepository detallesUsuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public DtoRespuestaUsuario registrarUsuario(DtoRegistroUsuario dato, Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        if (usuarioLogueado.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("No tiene permiso para realizar esta acción.");
        }
        String errores = ValidationUsuario.validarCamposEnBlanco(dato);
        if (!errores.equals("{}")) {
            throw new IllegalArgumentException("Campos inválidos" + errores);
        }
        if (usuarioRepository.findByEmail(dato.email()) != null) {
            throw new ObjectAlreadyExistsException("El email ya está en uso");
        }

        if (dato.role() == UserRole.ADMIN) {
            var rol = usuarioRepository.findByRole(dato.role());
            if (rol) {
                throw new ObjectAlreadyExistsException("¡Ya existe un usuario ADMIN, no se admiten más administradores!");
            }
        }

        if (dato.clinica() == null || dato.clinica().getId() == null) {
            throw new RelacionNoValidaException("Debe especificar una clínica válida");
        }

        //try {
            Usuario usuario = new Usuario();
            usuario.setEmail(dato.email());
            usuario.setPassword(passwordEncoder.encode(dato.password()));
            usuario.setActivo(true);
            usuario.setRole(dato.role());
            usuario.setClinica(dato.clinica());
            usuario.setConsultorio(dato.consultorio());
            usuario = usuarioRepository.save(usuario);

            DetallesUsuario detallesUsuario = new DetallesUsuario();
            detallesUsuario.setApellido(dato.apellido());
            detallesUsuario.setNombre(dato.nombre());
            detallesUsuario.setEspecialidad(dato.especialidad());
            detallesUsuario.setNumeroMatricula(dato.numeroMatricula());
            detallesUsuario.setTelefono(dato.telefono());
            detallesUsuario.setUsuario(usuario);
            detallesUsuarioRepository.save(detallesUsuario);

            return new DtoRespuestaUsuario(usuario, detallesUsuario);
//        } catch (DataIntegrityViolationException e) {
//            throw new RelacionNoValidaException("Error al guardar el usuario: Verifica que la clínica exista");
//        }
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
        if (usuarioLogueado.getRole() == UserRole.MEDICO && usuarioLogueado.getId() != dato.id()) {
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
