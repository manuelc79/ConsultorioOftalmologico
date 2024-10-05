package com.consultorio.oftalmologico.controllers;

import com.consultorio.oftalmologico.domain.dto.DtoDni;
import com.consultorio.oftalmologico.domain.dto.paciente.DtoModificaPaciente;
import com.consultorio.oftalmologico.domain.dto.paciente.DtoRegistroPaciente;
import com.consultorio.oftalmologico.domain.services.PacienteService;
import com.consultorio.oftalmologico.infraestructure.errors.errorsDto.DtoRespuestaErrores;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
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

    @GetMapping
    public ResponseEntity listaPacientes(Pageable pageable) {
        var pacientes = pacienteService.listarPacientes(pageable);
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
