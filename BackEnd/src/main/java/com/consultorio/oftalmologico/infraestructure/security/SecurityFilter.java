package com.consultorio.oftalmologico.infraestructure.security;

import com.consultorio.oftalmologico.domain.repository.MedicoRepository;
import com.consultorio.oftalmologico.domain.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Obtenemos el token del header
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null) {
            String token = authHeader.replace("Bearer ", "");

            var userName = tokenService.getSubject(token); // Extrae el nombre del usuario
            if (userName != null){
                // token válido
                var usuario = medicoRepository.findByEmail(userName);
                var authentication = new UsernamePasswordAuthenticationToken(usuario, null,
                        usuario.getAuthorities()); // forzamos el inicio
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
