package com.consultorio.oftalmologico.presentation.controllers;

import com.consultorio.oftalmologico.presentation.dto.medico.DtoAutenticarUsuario;
import com.consultorio.oftalmologico.presentation.dto.token.DtoJwtToken;
import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;
import com.consultorio.oftalmologico.application.services.TokenService;
import com.consultorio.oftalmologico.infraestructure.errors.errorsDto.DtoRespuestaErrores;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity autenticarMedico(@RequestBody @Valid DtoAutenticarUsuario dato) {
        if (dato.email() == null || dato.email().isEmpty()) {
            return ResponseEntity.badRequest().body(new DtoRespuestaErrores(HttpStatus.BAD_REQUEST.toString(),
                    "El correo no debe estar vacío"));
        }
        if (dato.password() == null || dato.password().isEmpty()) {
            return ResponseEntity.badRequest().body(new DtoRespuestaErrores(HttpStatus.BAD_REQUEST.toString(),
                    "La contraseña no debe estar en blanco"));
        }
        try {
            Authentication authToken = new UsernamePasswordAuthenticationToken(dato.email(), dato.password());
            var medicoAutenticado = authenticationManager.authenticate(authToken);
            var JWTtoken = tokenService.generarToken((Usuario) medicoAutenticado.getPrincipal());
            return ResponseEntity.ok(new DtoJwtToken(JWTtoken, ((Usuario)  medicoAutenticado.getPrincipal()).getId()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new DtoRespuestaErrores(
                    HttpStatus.UNAUTHORIZED.toString(), "Credenciales invalidas"));
        }
    }
}
