package com.consultorio.oftalmologico.presentation.controllers;

import com.consultorio.oftalmologico.presentation.dto.DtoBuscarPorId;
import com.consultorio.oftalmologico.presentation.dto.DtoDni;
import com.consultorio.oftalmologico.presentation.dto.consultorio.DtoBuscarPorUsuarioId;
import com.consultorio.oftalmologico.presentation.dto.paciente.DtoModificaPaciente;
import com.consultorio.oftalmologico.presentation.dto.paciente.DtoRegistroPaciente;
import com.consultorio.oftalmologico.application.services.PacienteService;
import com.consultorio.oftalmologico.infraestructure.errors.errorsDto.DtoRespuestaErrores;
import com.consultorio.oftalmologico.presentation.dto.paciente.DtoRespuestaPaciente;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@PreAuthorize("hasAnyRole('DIRECTOR', 'MEDICO')")
@RequestMapping("/api/paciente")
public class PacienteController {
    @Autowired
    PacienteService pacienteService;

    @PostMapping
    @Transactional
    public ResponseEntity<?> crearPaciente(@RequestBody @Valid DtoRegistroPaciente dato, Authentication authentication) {
        var paciente = pacienteService.crearPaciente(dato, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(paciente);
    }

    @GetMapping // Devuelve un listado de los pacientes discriminados por clínica
    public ResponseEntity<?> listaPacientes(Pageable pageable, Authentication authentication) {
        var pacientes = pacienteService.listarPacientes(pageable, authentication);
        return ResponseEntity.ok(pacientes.getContent());
    }
    
    @PostMapping("/find")
    public ResponseEntity<?> buscarPorDni(@RequestBody DtoDni dni, Authentication authentication) {
        try {
            var paciente = pacienteService.buscarPorDni(dni.dni(), authentication);
            return ResponseEntity.ok(paciente);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(),
                            "Paciente no encontrado"));
        }
    }

    @GetMapping("/find/by-user")
    public ResponseEntity<List<DtoRespuestaPaciente>>listarPacientePorUsuario(Authentication authentication) {
        List<DtoRespuestaPaciente> pacientes = pacienteService.listarPacientesPorUsuario(authentication);
        return ResponseEntity.ok(pacientes);
    }

    @DeleteMapping("/delete")
    @Transactional
    public ResponseEntity<?> eliminaPaciente(@RequestBody DtoDni dni, Authentication authentication) {
        Boolean eliminado = pacienteService.eliminarPaciente(dni.dni(), authentication);
        if (eliminado) {
            return ResponseEntity.ok("Paciente eliminado correctamente");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DtoRespuestaErrores(
                HttpStatus.NOT_FOUND.toString(), "Paciente inexistente"));
    }

    @PutMapping("/restore")
    @Transactional
    public ResponseEntity<?> recuperarPaciente(@RequestBody DtoDni dni, Authentication authentication) {
        Boolean recuperado = pacienteService.recuperarPaceinte(dni.dni(), authentication);
        if (recuperado) {
            return ResponseEntity.ok("Paciente recuperado correctamente");
        }
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body( new DtoRespuestaErrores(
                HttpStatus.NOT_FOUND.toString(), "Paciente inexistente"));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<?> modificarPaciente(@RequestBody @Valid DtoModificaPaciente dato, Authentication authentication) {
            var paciente = pacienteService.modificarPaciente(dato, authentication);
            return ResponseEntity.ok(paciente);
    }
}
