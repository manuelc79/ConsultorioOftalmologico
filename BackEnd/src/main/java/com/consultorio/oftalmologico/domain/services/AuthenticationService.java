package com.consultorio.oftalmologico.domain.services;

import com.consultorio.oftalmologico.domain.repository.MedicoRepository;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.EntidadNoEncontradaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {
    @Autowired
    private MedicoRepository medicoRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userMail = medicoRepository.findByEmail(username);
        if (userMail != null) {
            return userMail;
        }
        throw new EntidadNoEncontradaException("Usuario no encontrado");
    }
}
