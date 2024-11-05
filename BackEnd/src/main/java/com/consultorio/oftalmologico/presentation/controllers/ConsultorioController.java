package com.consultorio.oftalmologico.presentation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.consultorio.oftalmologico.application.services.ConsultorioService;
import com.consultorio.oftalmologico.infraestructure.errors.errorsDto.DtoRespuestaErrores;
import com.consultorio.oftalmologico.presentation.dto.DtoBuscarPorId;
import com.consultorio.oftalmologico.presentation.dto.consultorio.DtoBuscarPorUsuarioId;
import com.consultorio.oftalmologico.presentation.dto.consultorio.DtoModificaConsultorio;
import com.consultorio.oftalmologico.presentation.dto.consultorio.DtoRegistroConsultorio;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/consultorio")
public class ConsultorioController {
    @Autowired
    private ConsultorioService consultorioService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> nuevoConsultorio(@RequestBody @Valid DtoRegistroConsultorio dato, Authentication authentication){
        var consultorio = consultorioService.crearConsultorio(dato, authentication);
        return ResponseEntity.ok(consultorio);
    }

    @PostMapping("/find")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'MEDICO')")
    public ResponseEntity<?> buscarConsultorio(@RequestBody DtoBuscarPorUsuarioId dato, Authentication authentication) {
        var consultorio = consultorioService.buscarConsultorio(dato.usuarioId(), authentication);
        return ResponseEntity.ok(consultorio);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'MEDICO')")
    @Transactional
    public ResponseEntity<?> modificarConsultorio (@RequestBody DtoModificaConsultorio dato, Authentication authentication) {
            var consultorio = consultorioService.modificaConsultorio(dato, authentication);
            return ResponseEntity.ok(consultorio);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> eliminarConsutorio(@RequestBody DtoBuscarPorId dato, Authentication authentication) {
        Boolean consultorio = consultorioService.eliminarConsultorio(dato.id(), authentication);
        DtoRespuestaErrores respuesta;
        if (!consultorio){
            respuesta = new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(), "Consultorio inexistente");
        } else {
            respuesta = new DtoRespuestaErrores(HttpStatus.OK.toString(),"Consultorio eliminado correctamente");
        }

        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/restore")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> restaurarConsultorio(@RequestBody DtoBuscarPorId dato, Authentication authentication) {
        Boolean consultorio = consultorioService.restaurarConsultorio(dato.id(), authentication);
        DtoRespuestaErrores respuesta;
        if (!consultorio) {
            respuesta = new DtoRespuestaErrores(
                            HttpStatus.NOT_FOUND.toString(), "Consultorio inexistente o actualmente activo");
        } else {
            respuesta = new DtoRespuestaErrores(
                    HttpStatus.OK.toString(),
                    "Consultorio restaurado correctamente"
            );
        }

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR')")
    public ResponseEntity<?> listarConsultorios(Pageable pageable, Authentication authentication) {
        var consultorios = consultorioService.listarConsultrios(pageable, authentication);
        if (consultorios == null) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se pudo obtener la lista de Consultorios");
        }
        return ResponseEntity.ok(consultorios.getContent());
    }
}
