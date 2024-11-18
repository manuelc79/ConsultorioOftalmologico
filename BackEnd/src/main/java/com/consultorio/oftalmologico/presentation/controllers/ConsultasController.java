package com.consultorio.oftalmologico.presentation.controllers;

import java.nio.file.AccessDeniedException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import com.consultorio.oftalmologico.application.services.ConsultasService;
import com.consultorio.oftalmologico.infraestructure.errors.errorsDto.DtoRespuestaErrores;
import com.consultorio.oftalmologico.presentation.dto.DtoBuscaPorFecha;
import com.consultorio.oftalmologico.presentation.dto.DtoBuscarPorId;
import com.consultorio.oftalmologico.presentation.dto.DtoDni;
import com.consultorio.oftalmologico.presentation.dto.consulta.DtoModificaConsulta;
import com.consultorio.oftalmologico.presentation.dto.consulta.DtoNuevaConsulta;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/consulta")
public class ConsultasController {

    private final ConsultasService consultasService;

    public ConsultasController(ConsultasService consultasService) {
        this.consultasService = consultasService;
    }

    /**
     * Crea una nueva consulta.
     * 
     * @param dato          Datos de la nueva consulta.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con la consulta creada.
     * @throws AccessDeniedException si el usuario no tiene permiso.
     */
    @PostMapping
    @PreAuthorize("hasRole('MEDICO')")
    @Transactional
    public ResponseEntity<?> nuevaConsulta(@RequestBody @Valid DtoNuevaConsulta dato, Authentication authentication) throws AccessDeniedException {
        var consulta = consultasService.guardarConsulta(dato, authentication);
        return ResponseEntity.ok(consulta);
    }

    /**
     * Modifica una consulta existente.
     * 
     * @param dato          Datos de la consulta a modificar.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con la consulta modificada.
     * @throws AccessDeniedException si el usuario no tiene permiso.
     */
    @PutMapping
    @Transactional
    public ResponseEntity<?> modificaConsulta(@RequestBody DtoModificaConsulta dato, Authentication authentication) throws AccessDeniedException {
        var consulta = consultasService.modificaConsulta(dato, authentication);
        return ResponseEntity.ok(consulta);
    }

    /**
     * Lista todas las consultas paginadas.
     * 
     * @param page          Información de paginación.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con la lista de consultas.
     */
    @GetMapping
    public ResponseEntity<?> listar(@PageableDefault(size = 10, sort = {"fechaConsulta"}) Pageable page, Authentication authentication) {
        var consultas = consultasService.consultar(page, authentication);
        return ResponseEntity.ok(consultas.getContent());
    }

    @PostMapping("/find")
    public ResponseEntity<?> mostrarPorPaciente(@RequestBody DtoBuscarPorId dato, Authentication authentication) {
        var consulta = consultasService.buscarConsultaId(dato.id(), authentication);
        if (consulta == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(), "Consulta no encontrada"));
        }
        return ResponseEntity.ok(consulta);
    }

    @PostMapping("/find/paciente")
    public ResponseEntity<?> listarPorPaciente(@PageableDefault(size = 10, sort = {"fechaConsulta"}) @RequestBody DtoDni dato, Pageable pageable, Authentication authentication) {
        var consulta = consultasService.listarPorPaciente(dato.dni(), pageable, authentication);
        return ResponseEntity.ok(consulta.getContent());
    }

    @PostMapping("/find/fecha")
    public ResponseEntity<?> listarPorFecha(@PageableDefault(sort = {"fechaConsulta"}) @RequestBody DtoBuscaPorFecha dato, Pageable pageable, Authentication authentication) {
        var consulta = consultasService.listarPorFecha(dato, pageable, authentication);
        return ResponseEntity.ok(consulta.getContent());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> eliminarConsulta(@RequestBody DtoBuscarPorId idConsulta, Authentication authentication) {
        Boolean eliminado = consultasService.eliminarConsulta(idConsulta.id(), authentication);
        if (eliminado) {
            return ResponseEntity.ok().body("Consulta Eliminada");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(), "Consulta no encontrada"));
    }
}
