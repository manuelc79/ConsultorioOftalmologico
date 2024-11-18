package com.consultorio.oftalmologico.presentation.controllers;

import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;
import com.consultorio.oftalmologico.infraestructure.errors.errorsDto.DtoRespuestaErrores;

import com.consultorio.oftalmologico.application.services.TokenService;
import com.consultorio.oftalmologico.application.services.UsuarioService;

import com.consultorio.oftalmologico.presentation.dto.medico.DtoAutenticarUsuario;
import com.consultorio.oftalmologico.presentation.dto.token.DtoJwtToken;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final TokenService tokenService;
    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;

    public LoginController(TokenService tokenService, UsuarioService usuarioService, AuthenticationManager authenticationManager) {
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping
    public ResponseEntity<?> autenticarMedico(@RequestBody @Valid DtoAutenticarUsuario dato) {
        if (dato.email() == null || dato.email().isEmpty()) {
            return ResponseEntity.badRequest().body(new DtoRespuestaErrores(HttpStatus.BAD_REQUEST.toString(), "El correo no debe estar vacío"));
        }
        if (dato.password() == null || dato.password().isEmpty()) {
            return ResponseEntity.badRequest().body(new DtoRespuestaErrores(HttpStatus.BAD_REQUEST.toString(), "La contraseña no debe estar en blanco"));
        }
        try {
            Authentication authToken = new UsernamePasswordAuthenticationToken(dato.email(), dato.password());
            Authentication medicoAutenticado = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(medicoAutenticado);
            var jwtToken = tokenService.generarToken((Usuario) medicoAutenticado.getPrincipal());
            return ResponseEntity.ok(new DtoJwtToken(jwtToken, ((Usuario) medicoAutenticado.getPrincipal()).getId()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new DtoRespuestaErrores(HttpStatus.UNAUTHORIZED.toString(), "Credenciales invalidas"));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new DtoRespuestaErrores(HttpStatus.UNAUTHORIZED.toString(), "Error de autenticación"));
        }
    }
}
