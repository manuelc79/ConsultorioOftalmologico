package com.consultorio.oftalmologico.presentation.controllers;

import com.consultorio.oftalmologico.presentation.dto.DtoBuscarPorId;
import com.consultorio.oftalmologico.presentation.dto.medico.DtoModificaUsuario;
import com.consultorio.oftalmologico.presentation.dto.medico.DtoRegistroUsuario;
import com.consultorio.oftalmologico.application.services.UsuarioService;
import com.consultorio.oftalmologico.infraestructure.validations.ValidationUsuario;
import com.consultorio.oftalmologico.infraestructure.errors.errorsDto.DtoRespuestaErrores;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.EntidadNoEncontradaException;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.ObjectAlreadyExistsException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medico")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> registrarUsuario(@RequestBody @Valid DtoRegistroUsuario dtoRegistroUsuario) {
        String errores = ValidationUsuario.validarCamposEnBlanco(dtoRegistroUsuario);
        if (!errores.equals("{}")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DtoRespuestaErrores(
                    HttpStatus.BAD_REQUEST.toString(), "Campos inválidos" + errores));
        }
        try {
            var usuario = usuarioService.registrarUsuario(dtoRegistroUsuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } catch (ObjectAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DtoRespuestaErrores(
                    HttpStatus.BAD_REQUEST.toString(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DtoRespuestaErrores(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error al crear el Usuario"));
        }
    }

    @PostMapping("/find")
    public ResponseEntity<?> buscarUsuario(@RequestBody DtoBuscarPorId dato, String email) {
            var usuario = usuarioService.buscarUsuario(dato.id(), email);
            if (usuario == null) {
                throw new EntidadNoEncontradaException("Usuario no encontrado");
            }
            return ResponseEntity.ok(usuario);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<?> modificaUsuario(@RequestBody DtoModificaUsuario dato, String email) {
            var usuario = usuarioService.modificaUsuario(dato, email);
            if (usuario == null) {
                throw new EntidadNoEncontradaException("Usuario no encontrado");
            }
            return ResponseEntity.ok(usuario);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> eliminarUsuario(@RequestBody DtoBuscarPorId dato, Long clinicaId) {
        Boolean usuario = usuarioService.eliminarUsuario(dato.id(), clinicaId);
        if (!usuario) {
            throw new EntidadNoEncontradaException("Usuario no encontrado");
        }
        return ResponseEntity.ok("Usuario eliminado Correctamente");
    }

    @PutMapping("/restore")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> restaurarUsuario(@RequestBody DtoBuscarPorId dato, Long clinica) {
        Boolean usuario = usuarioService.restauraUsuario(dato.id(), clinica);
        if (!usuario) {
            throw new EntidadNoEncontradaException("Usuario no encontrado");
        }
        return ResponseEntity.ok("Usuario restaurado correctamente");
    }
}
