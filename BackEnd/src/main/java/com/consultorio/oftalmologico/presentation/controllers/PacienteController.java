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
    public ResponseEntity<?> crearPaciente(@RequestBody @Valid DtoRegistroPaciente dato) {
        var paciente = pacienteService.crearPaciente(dato);
        return ResponseEntity.status(HttpStatus.CREATED).body(paciente);
    }

    @GetMapping // Devuelve un listado de los pacientes discriminados por clinica
    public ResponseEntity<?> listaPacientes(Pageable pageable, String email ) {
        var pacientes = pacienteService.listarPacientes(pageable, email);
        return ResponseEntity.ok(pacientes.getContent());
    }
    
    @PostMapping("/find")
    public ResponseEntity<?> buscarPorDni(@RequestBody DtoDni dni) {
        try {
            var paciente = pacienteService.buscarPorDni(dni.dni());
            return ResponseEntity.ok(paciente);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(),
                            "Paciente no encontrado"));
        }
    }

    @PostMapping("/find/by-user")
    public ResponseEntity<List<DtoRespuestaPaciente>>listarPacientePorUsuario(@RequestBody DtoBuscarPorId dato) {
        List<DtoRespuestaPaciente> pacientes = pacienteService.listarPacientesPorUsuario(dato.id());
        return ResponseEntity.ok(pacientes);
    }

    @DeleteMapping("/delete")
    @Transactional
    public ResponseEntity<?> eliminaPaciente(@RequestBody DtoDni dni) {
        Boolean eliminado = pacienteService.eliminarPaciente(dni.dni());
        if (eliminado) {
            return ResponseEntity.ok("Paciente eliminado correctamente");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DtoRespuestaErrores(
                HttpStatus.NOT_FOUND.toString(), "Paciente inexistente"));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<?> modificarPaciente(@RequestBody @Valid DtoModificaPaciente dato) {
        try {
            var paciente = pacienteService.modificarPaciente(dato);
            return ResponseEntity.ok(paciente);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(),
                            "Paciente no encontrado"));
        }
    }
}
