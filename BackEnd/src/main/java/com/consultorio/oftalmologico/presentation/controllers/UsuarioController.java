package com.consultorio.oftalmologico.presentation.controllers;

import com.consultorio.oftalmologico.presentation.dto.medico.DtoBuscarUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.consultorio.oftalmologico.application.services.UsuarioService;
import com.consultorio.oftalmologico.infraestructure.errors.errorsDto.DtoRespuestaErrores;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.EntidadNoEncontradaException;
import com.consultorio.oftalmologico.infraestructure.validations.ValidationUsuario;
import com.consultorio.oftalmologico.presentation.dto.DtoBuscarPorId;
import com.consultorio.oftalmologico.presentation.dto.medico.DtoModificaUsuario;
import com.consultorio.oftalmologico.presentation.dto.medico.DtoRegistroUsuario;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/medico")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> registrarUsuario(@RequestBody @Valid DtoRegistroUsuario dtoRegistroUsuario, Authentication authentication) {
        String errores = ValidationUsuario.validarCamposEnBlanco(dtoRegistroUsuario);
        if (!errores.equals("{}")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DtoRespuestaErrores(
                    HttpStatus.BAD_REQUEST.toString(), "Campos inválidos" + errores));
        }
        var usuario = usuarioService.registrarUsuario(dtoRegistroUsuario, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @PostMapping("/find")
    public ResponseEntity<?> buscarUsuario(@RequestBody DtoBuscarPorId dato, Authentication authentication) {
            var usuario = usuarioService.buscarUsuario(dato.id(), authentication);
            if (usuario == null) {
                throw new EntidadNoEncontradaException("Usuario no encontrado");
            }
            return ResponseEntity.ok(usuario);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<?> modificaUsuario(@RequestBody DtoModificaUsuario dato, Authentication authentication) {
            var usuario = usuarioService.modificaUsuario(dato, authentication);
            if (usuario == null) {
                throw new EntidadNoEncontradaException("Usuario no encontrado");
            }
            return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> eliminarUsuario(@RequestBody DtoBuscarPorId dato, Authentication authentication) {
        Boolean usuario = usuarioService.eliminarUsuario(dato.id(), authentication);
        if (!usuario) {
            throw new EntidadNoEncontradaException("Usuario no encontrado");
        }
        return ResponseEntity.ok("Usuario eliminado Correctamente");
    }

    @PutMapping("/restore")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> restaurarUsuario(@RequestBody DtoBuscarPorId dato, Authentication authentication) {
        Boolean usuario = usuarioService.restauraUsuario(dato.id(), authentication);
        if (!usuario) {
            throw new EntidadNoEncontradaException("Usuario no encontrado o actualmente en actividad");
        }
        return ResponseEntity.ok("Usuario restaurado correctamente");
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR')")
    public ResponseEntity<?> listarUsuarios(Authentication authentication) {
        var usuarios = usuarioService.listarUsuarios(authentication);
        if (usuarios == null ) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se pudo obtener la lista de usuarios");
        }

        return ResponseEntity.ok(usuarios);
    }
}
