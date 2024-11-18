package com.consultorio.oftalmologico.presentation.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.consultorio.oftalmologico.application.services.UsuarioService;
import com.consultorio.oftalmologico.infraestructure.errors.errorsDto.DtoRespuestaErrores;
import com.consultorio.oftalmologico.infraestructure.validations.ValidationUsuario;
import com.consultorio.oftalmologico.presentation.dto.DtoBuscarPorId;
import com.consultorio.oftalmologico.presentation.dto.medico.DtoModificaUsuario;
import com.consultorio.oftalmologico.presentation.dto.medico.DtoRegistroUsuario;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/medico")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Registra un nuevo usuario.
     * 
     * @param dtoRegistroUsuario Datos del nuevo usuario.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con el usuario creado.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> registrarUsuario(@RequestBody @Valid DtoRegistroUsuario dtoRegistroUsuario, Authentication authentication) {
        String errores = ValidationUsuario.validarCamposEnBlanco(dtoRegistroUsuario);
        if (!errores.equals("{}")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DtoRespuestaErrores(HttpStatus.BAD_REQUEST.toString(), "Campos inválidos" + errores));
        }
        var usuario = usuarioService.registrarUsuario(dtoRegistroUsuario, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    /**
     * Busca un usuario por ID.
     * 
     * @param dato Datos de búsqueda.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con el usuario encontrado o un error.
     */
    @PostMapping("/find")
    public ResponseEntity<?> buscarUsuario(@RequestBody DtoBuscarPorId dato, Authentication authentication) {
        var usuario = usuarioService.buscarUsuario(dato.id(), authentication);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(), "Usuario no encontrado"));
        }
        return ResponseEntity.ok(usuario);
    }

    /**
     * Modifica un usuario existente.
     * 
     * @param dato Datos del usuario a modificar.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con el usuario modificado.
     */
    @PutMapping
    @Transactional
    public ResponseEntity<?> modificarUsuario(@RequestBody DtoModificaUsuario dato, Authentication authentication) {
        var usuario = usuarioService.modificaUsuario(dato, authentication);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(), "Usuario no encontrado"));
        }
        return ResponseEntity.ok(usuario);
    }

    /**
     * Elimina un usuario.
     * 
     * @param dato Datos del usuario a eliminar.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con el resultado de la eliminación.
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> eliminarUsuario(@RequestBody DtoBuscarPorId dato, Authentication authentication) {
        if (!usuarioService.eliminarUsuario(dato.id(), authentication)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(), "Usuario no encontrado"));
        }
        return ResponseEntity.ok(new DtoRespuestaErrores(HttpStatus.OK.toString(), "Usuario eliminado correctamente"));
    }

    @PutMapping("/restore")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> restaurarUsuario(@RequestBody DtoBuscarPorId dato, Authentication authentication) {
        if (!usuarioService.restauraUsuario(dato.id(), authentication)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(), "Usuario no encontrado o actualmente en actividad"));
        }
        return ResponseEntity.ok(new DtoRespuestaErrores(HttpStatus.OK.toString(), "Usuario restaurado correctamente"));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR')")
    public ResponseEntity<?> listarUsuarios(Authentication authentication) {
        var usuarios = usuarioService.listarUsuarios(authentication);
        if (usuarios == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new DtoRespuestaErrores(HttpStatus.FORBIDDEN.toString(), "No se pudo obtener la lista de usuarios"));
        }
        return ResponseEntity.ok(usuarios);
    }
}
