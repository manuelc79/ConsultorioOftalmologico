package com.consultorio.oftalmologico.presentation.controllers;

import java.nio.file.AccessDeniedException;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.consultorio.oftalmologico.application.services.PacienteService;
import com.consultorio.oftalmologico.infraestructure.errors.errorsDto.DtoRespuestaErrores;
import com.consultorio.oftalmologico.presentation.dto.DtoDni;
import com.consultorio.oftalmologico.presentation.dto.paciente.DtoModificaPaciente;
import com.consultorio.oftalmologico.presentation.dto.paciente.DtoRegistroPaciente;

import jakarta.validation.Valid;

@RestController
@PreAuthorize("hasAnyRole('DIRECTOR', 'MEDICO')")
@RequestMapping("/api/paciente")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    /**
     * Crea un nuevo paciente.
     * 
     * @param dato          Datos del nuevo paciente.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con el paciente creado.
     */
    @PostMapping
    @Transactional
    public ResponseEntity<?> crearPaciente(@RequestBody @Valid DtoRegistroPaciente dato, Authentication authentication) {
        var paciente = pacienteService.crearPaciente(dato, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(paciente);
    }

    /**
     * Modifica un paciente existente.
     * 
     * @param dato          Datos del paciente a modificar.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con el paciente modificado.
     */
    @PutMapping
    @Transactional
    public ResponseEntity<?> modificarPaciente(@RequestBody @Valid DtoModificaPaciente dato, Authentication authentication) throws AccessDeniedException {
        var paciente = pacienteService.modificarPaciente(dato, authentication);
        return ResponseEntity.ok(paciente);
    }

    /**
     * Elimina un paciente.
     * 
     * @param dni          Datos del paciente a eliminar.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con el resultado de la eliminación.
     */
    @DeleteMapping("/delete")
    @Transactional
    public ResponseEntity<?> eliminarPaciente(@RequestBody DtoDni dni, Authentication authentication) throws AccessDeniedException {
        boolean eliminado = pacienteService.eliminarPaciente(dni.dni(), authentication);
        if (eliminado) {
            return ResponseEntity.ok(new DtoRespuestaErrores(HttpStatus.OK.toString(), "Paciente eliminado correctamente"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(), "Paciente inexistente"));
    }

    /**
     * Recupera un paciente eliminado.
     * 
     * @param dni          Datos del paciente a recuperar.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con el resultado de la recuperación.
     */
    @PutMapping("/restore")
    @Transactional
    public ResponseEntity<?> recuperarPaciente(@RequestBody DtoDni dni, Authentication authentication) throws AccessDeniedException {
        boolean recuperado = pacienteService.recuperarPaciente(dni.dni(), authentication);
        if (recuperado) {
            return ResponseEntity.ok(new DtoRespuestaErrores(HttpStatus.OK.toString(), "Paciente recuperado correctamente"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(), "Paciente inexistente"));
    }

    /**
     * Lista todos los pacientes.
     * 
     * @param pageable      Información de paginación.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con la lista de pacientes.
     */
    @GetMapping
    public ResponseEntity<?> listaPacientes(Pageable pageable, Authentication authentication) {
        var pacientes = pacienteService.listarPacientes(pageable, authentication);
        return ResponseEntity.ok(pacientes.getContent());
    }

    /**
     * Busca un paciente por DNI.
     * 
     * @param dato          Datos de búsqueda.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con el paciente encontrado.
     */
    @PostMapping("/find/paciente")
    public ResponseEntity<?> buscarPorDni(@RequestBody DtoDni dato, Authentication authentication) throws AccessDeniedException {
        var paciente = pacienteService.buscarPorDni(dato.dni(), authentication);
        return ResponseEntity.ok(paciente);
    }

    @GetMapping("/find/by-user")
    public ResponseEntity<?> listarPacientePorUsuario(Authentication authentication) {
        var pacientes = pacienteService.listarPacientesPorUsuario(authentication);
        return ResponseEntity.ok(pacientes);
    }

}
