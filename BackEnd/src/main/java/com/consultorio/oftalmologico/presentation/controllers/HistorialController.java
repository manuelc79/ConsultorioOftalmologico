package com.consultorio.oftalmologico.presentation.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.consultorio.oftalmologico.domain.entities.RegistroActividad;
import com.consultorio.oftalmologico.domain.repository.RegistroActividadRepository;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/historial")
public class HistorialController {

    private final RegistroActividadRepository registroActividadRepository;

    public HistorialController(RegistroActividadRepository registroActividadRepository) {
        this.registroActividadRepository = registroActividadRepository;
    }

    @GetMapping
    public ResponseEntity<List<RegistroActividad>> obtenerHistorial() {
        List<RegistroActividad> historial = registroActividadRepository.findAll();
        return ResponseEntity.ok(historial);
    }
}
