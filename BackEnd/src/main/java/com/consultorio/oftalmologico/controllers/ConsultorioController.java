package com.consultorio.oftalmologico.controllers;

import com.consultorio.oftalmologico.domain.dto.DtoBuscarPorId;
import com.consultorio.oftalmologico.domain.dto.consultorio.DtoBuscarPorMedicoId;
import com.consultorio.oftalmologico.domain.dto.consultorio.DtoModificaConsultorio;
import com.consultorio.oftalmologico.domain.dto.consultorio.DtoRegistroConsultorio;
import com.consultorio.oftalmologico.domain.services.ConsultorioService;
import com.consultorio.oftalmologico.infraestructure.errors.errorsDto.DtoRespuestaErrores;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consultorio")
public class ConsultorioController {
    @Autowired
    private ConsultorioService consultorioService;

    @PostMapping
    @Transactional
    public ResponseEntity<?> nuevoConsultorio(@RequestBody @Valid DtoRegistroConsultorio dato){
        var consultorio = consultorioService.crearConsultorio(dato);
        return ResponseEntity.ok(consultorio);
    }

    @PostMapping("/find")
    public ResponseEntity<?> buscarConsultorio(@RequestBody DtoBuscarPorMedicoId dato) {
        var consultorio = consultorioService.buscarConsultorio(dato.medicoId());
        return ResponseEntity.ok(consultorio);
    }

    @PutMapping
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
