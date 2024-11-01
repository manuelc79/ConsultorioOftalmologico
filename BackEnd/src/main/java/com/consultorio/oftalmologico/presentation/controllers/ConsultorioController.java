package com.consultorio.oftalmologico.presentation.controllers;

import com.consultorio.oftalmologico.presentation.dto.DtoBuscarPorId;
import com.consultorio.oftalmologico.presentation.dto.consultorio.DtoBuscarPorUsuarioId;
import com.consultorio.oftalmologico.presentation.dto.consultorio.DtoModificaConsultorio;
import com.consultorio.oftalmologico.presentation.dto.consultorio.DtoRegistroConsultorio;
import com.consultorio.oftalmologico.application.services.ConsultorioService;
import com.consultorio.oftalmologico.infraestructure.errors.errorsDto.DtoRespuestaErrores;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consultorio")
public class ConsultorioController {
    @Autowired
    private ConsultorioService consultorioService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> nuevoConsultorio(@RequestBody @Valid DtoRegistroConsultorio dato, Long clinicaId){
        var consultorio = consultorioService.crearConsultorio(dato, clinicaId);
        return ResponseEntity.ok(consultorio);
    }

    @PostMapping("/find")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'MEDICO')")
    public ResponseEntity<?> buscarConsultorio(@RequestBody DtoBuscarPorUsuarioId dato) {
        var consultorio = consultorioService.buscarConsultorio(dato.usuarioId());
        return ResponseEntity.ok(consultorio);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'MEDICO')")
    @Transactional
    public ResponseEntity<?> modificarConsultorio (@RequestBody DtoModificaConsultorio dato) {
        try {
            var consultorio = consultorioService.modificaConsultorio(dato);
            return ResponseEntity.ok(consultorio);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DtoRespuestaErrores(
                            HttpStatus.NOT_FOUND.toString(), "Consultorio Inexistente")
            );
        }
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> eliminarConsutorio(@RequestBody DtoBuscarPorId dato) {
        Boolean consultorio = consultorioService.eliminarConsultorio(dato.id());
        if (!consultorio){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body((new DtoRespuestaErrores(
                    HttpStatus.NOT_FOUND.toString(), "Consultorio inexistente")));
        }
        return ResponseEntity.ok("Consultorio eliminado correctamente");

    }
}
