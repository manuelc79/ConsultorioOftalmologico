package com.consultorio.oftalmologico.domain.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.consultorio.oftalmologico.domain.entities.medico.Medico;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.EntidadNoEncontradaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.secret}")
    private String apiSecret;

    public String generarToken(Medico medico) {

        try {
            Algorithm algorithm = Algorithm.HMAC256(apiSecret);
            var token = JWT.create()
                    .withIssuer("Oftalmologo API")
                    .withSubject(medico.getUsername())
                    .withClaim("id", medico.getId())
                    .withExpiresAt(generarFechaDeExpliracion())
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException();
        }
    }

    public String getSubject(String token) {
        if (token == null) {
            throw new RuntimeException();
        }
        DecodedJWT verifier = null;

        try {
            Algorithm algorithm = Algorithm.HMAC256(apiSecret);
            verifier = JWT.require(algorithm)
                    .withIssuer("Oftalmologo API")
                    .build()
                    .verify(token);
            verifier.getSubject();
        } catch (JWTCreationException exception) {
            throw new EntidadNoEncontradaException("El token ha expirado");
        }
        if (verifier == null) {
            throw new EntidadNoEncontradaException("Verificación invalida");
        }
        return verifier.getSubject();
    }

    private Instant generarFechaDeExpliracion() {
        return LocalDateTime.now().plusHours(6).toInstant(ZoneOffset.of("-03:00"));
    }
}
