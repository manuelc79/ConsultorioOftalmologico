package com.consultorio.oftalmologico.application.services;

import com.consultorio.oftalmologico.domain.enums.UserRole;
import com.consultorio.oftalmologico.presentation.dto.medico.DtoModificaUsuario;
import com.consultorio.oftalmologico.presentation.dto.medico.DtoRegistroUsuario;
import com.consultorio.oftalmologico.presentation.dto.medico.DtoRespuestaUsuario;
import com.consultorio.oftalmologico.domain.entities.usuario.DetallesUsuario;
import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;
import com.consultorio.oftalmologico.domain.repository.DetallesUsuarioRepository;
import com.consultorio.oftalmologico.domain.repository.UsuarioRepository;
import com.consultorio.oftalmologico.infraestructure.validations.ValidationUsuario;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.EntidadNoEncontradaException;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.ObjectAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DetallesUsuarioRepository detallesUsuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public DtoRespuestaUsuario registrarUsuario(DtoRegistroUsuario dato) {
        String errores = ValidationUsuario.validarCamposEnBlanco(dato);
        if (!errores.equals("{}")) {
            throw new IllegalArgumentException("Campos inválidos" + errores);
        }
        if (usuarioRepository.findByEmail(dato.email()) != null) {
            throw new ObjectAlreadyExistsException("El email ya está en uso");
        }

        if (dato.role() == UserRole.ADMIN) {
            var rol = usuarioRepository.findByRole(dato.role());
            if (rol){
                throw new ObjectAlreadyExistsException("¡Ya existe un usuario ADMIN, no se adminten más administradores!");
            }
        }

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
    }

    public DtoRespuestaUsuario buscarUsuario(Long id, String email) {
        var usuarioLoged = usuarioRepository.findByEmail(email);
        var clinicaId = usuarioLoged.getClinica().getId();
        if (usuarioLoged.getRole() == UserRole.ADMIN) {
            return usuarioRepository.findByUserId(id);
        }

        var usuario = usuarioRepository.findByIdAndActivo(id, clinicaId);
        if (usuario == null ){
            throw new EntidadNoEncontradaException("Usuario no Encontrado");
        }
        DetallesUsuario detallesUsuario = detallesUsuarioRepository.findByUsuarioId(id);
        return new DtoRespuestaUsuario(usuario, detallesUsuario);
    }

    public DtoRespuestaUsuario modificaUsuario(DtoModificaUsuario dato, String email) {
        var usuarioLoged = usuarioRepository.findByEmail(email);
        var clinicaId = usuarioLoged.getClinica().getId();

        var usuario = usuarioRepository.findByIdAndActivo(dato.id(), clinicaId);
        if (usuario == null) {
            throw new EntidadNoEncontradaException("Usuario no encontrado");
        }
        DetallesUsuario detallesUsuario = detallesUsuarioRepository.findByUsuarioId(usuario.getId());
        if (dato.password() != null && !dato.password().isBlank() ) {
            usuario.setPassword(passwordEncoder.encode(dato.password()));
        }
        if (dato.nombre() != null){
            detallesUsuario.setNombre(dato.nombre());
        }
        if (dato.apellido() != null){
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
        if (dato.role() != null && usuarioLoged.getRole() == UserRole.ADMIN) {
            usuario.setRole(dato.role());
        }
        if (dato.clinica() != null && usuarioLoged.getRole() == UserRole.ADMIN) {
            usuario.setClinica(dato.clinica());
        }
        if (dato.consultorio() != null && usuarioLoged.getRole() == UserRole.ADMIN) {
            usuario.setConsultorio(dato.consultorio());
        }
        usuarioRepository.save(usuario);
        detallesUsuarioRepository.save(detallesUsuario);
        return new DtoRespuestaUsuario(usuario, detallesUsuario);
    }

    public Boolean eliminarUsuario(Long id, Long clinicaId) {
        var usuario = usuarioRepository.findByIdAndActivo(id, clinicaId);
        if (usuario == null) {
            return false;
        }
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        return true;
    }

    public Boolean restauraUsuario(Long id, Long clinicaId) {
        var usuario = usuarioRepository.findByIdAndActivoFalse(id, clinicaId);
        if (usuario == null) {
            return false;
        }
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
        return true;

    }
}
