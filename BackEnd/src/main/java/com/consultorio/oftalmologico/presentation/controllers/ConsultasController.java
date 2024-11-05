package com.consultorio.oftalmologico.presentation.controllers;

import com.consultorio.oftalmologico.presentation.dto.DtoBuscaPorFecha;
import com.consultorio.oftalmologico.presentation.dto.DtoBuscarPorId;
import com.consultorio.oftalmologico.presentation.dto.DtoDni;
import com.consultorio.oftalmologico.presentation.dto.consulta.DtoModificaConsulta;
import com.consultorio.oftalmologico.presentation.dto.consulta.DtoNuevaConsulta;
import com.consultorio.oftalmologico.application.services.ConsultasService;
import com.consultorio.oftalmologico.infraestructure.errors.errorsDto.DtoRespuestaErrores;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/consulta")
public class ConsultasController {

    @Autowired
    ConsultasService consultasService;

    @PostMapping
    @PreAuthorize("hasRole('MEDICO')")
    @Transactional
    public ResponseEntity<?> nuevaConsulta(@RequestBody @Valid DtoNuevaConsulta dato, Authentication authentication) throws AccessDeniedException {
        var consulta = consultasService.guardarConsulta(dato, authentication);
        return ResponseEntity.ok(consulta);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<?> modificaConsulta(@RequestBody DtoModificaConsulta dato, Authentication authentication) throws AccessDeniedException {
        var consulta = consultasService.modificaConstulta(dato, authentication);
        return ResponseEntity.ok(consulta);
    }

    @GetMapping
    public ResponseEntity<?> listar(@PageableDefault(size = 10, sort = {"fechaConsulta"}) Pageable page, Authentication authentication) {
        var consultas = consultasService.consultar(page, authentication);
        return ResponseEntity.ok(consultas.getContent());
    }

    @PostMapping("/find")
    public ResponseEntity<?> mostrarPorPaciente (@RequestBody DtoBuscarPorId dato, Authentication authentication) {
        var consulta = consultasService.buscarConsultaId(dato.id(), authentication);
        return ResponseEntity.ok(consulta);
    }

    @PostMapping("/find/paciente")
    public ResponseEntity<?> listarPorPaciente(@PageableDefault(size = 10, sort = {"fechaConsulta"})
                                                   @RequestBody DtoDni dato, Pageable pageable, Authentication authentication) {
        var consulta = consultasService.listarPorPaciente(dato.dni(), pageable, authentication);
        return ResponseEntity.ok(consulta.getContent());
    }

    @PostMapping("/find/fecha")
    public ResponseEntity listarPorFecha(@PageableDefault(sort = {"fechaConsulta"})
                                             @RequestBody DtoBuscaPorFecha dato, Pageable pageable, Authentication authentication) {
        var consulta = consultasService.listarPorFecha(dato, pageable, authentication);
        return ResponseEntity.ok(consulta.getContent());
    }

    @DeleteMapping("/delete")
    public ResponseEntity eliminarConsulta(@RequestBody DtoBuscarPorId idConsulta, Authentication authentication) {
        Boolean eliminado = consultasService.eliminarConsulta(idConsulta.id(), authentication);
        if (eliminado) {
            return ResponseEntity.ok().body("Conuslta Eliminada");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(),
                        "Consulta no encontrada"));
    }
}
