package com.consultorio.oftalmologico.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;
import com.consultorio.oftalmologico.domain.enums.UserRole;
import com.consultorio.oftalmologico.domain.repository.UsuarioRepository;

@Component
public class StartupService implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Verificar si ya existe un usuario ADMIN
        if (!usuarioRepository.findByRole(UserRole.ADMIN)) {
            // Crear un nuevo usuario ADMIN
            Usuario adminUser = new Usuario();
            adminUser.setEmail("zikodevjuy@gmail.com");
            adminUser.setPassword(passwordEncoder.encode("admin1234"));
            adminUser.setActivo(true);
            adminUser.setRole(UserRole.ADMIN);
            adminUser.setClinica(null);
            adminUser.setConsultorio(null);
            
            // Guardar el usuario en la base de datos
            usuarioRepository.save(adminUser);
        }
    }
}
