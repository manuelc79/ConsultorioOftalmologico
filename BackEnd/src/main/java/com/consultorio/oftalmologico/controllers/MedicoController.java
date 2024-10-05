package com.consultorio.oftalmologico.controllers;

import com.consultorio.oftalmologico.domain.dto.DtoBuscarPorId;
import com.consultorio.oftalmologico.domain.dto.medico.DtoAutenticarMedico;
import com.consultorio.oftalmologico.domain.dto.medico.DtoModificaMedico;
import com.consultorio.oftalmologico.domain.dto.medico.DtoRegistroMedico;
import com.consultorio.oftalmologico.domain.services.MedicoService;
import com.consultorio.oftalmologico.domain.validations.ValidationMedico;
import com.consultorio.oftalmologico.infraestructure.errors.errorsDto.DtoRespuestaErrores;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.EntidadNoEncontradaException;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.ObjectAlreadyExistsException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medico")
public class MedicoController {
    @Autowired
    private MedicoService medicoService;

    @PostMapping
    @Transactional
    public ResponseEntity<?> registrarMedico(@RequestBody @Valid DtoRegistroMedico dtoRegistroMedico) {
        String errores = ValidationMedico.validarCamposEnBlanco(dtoRegistroMedico);
        if (!errores.equals("{}")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DtoRespuestaErrores(
                    HttpStatus.BAD_REQUEST.toString(), "Campos inválidos" + errores));
        }
        try {
            var medico = medicoService.registrarMedico(dtoRegistroMedico);
            return ResponseEntity.status(HttpStatus.CREATED).body(medico);
        } catch (ObjectAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DtoRespuestaErrores(
                    HttpStatus.BAD_REQUEST.toString(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DtoRespuestaErrores(
                    HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Error al crear el medico"));
        }
    }

    @PostMapping("/find")
    public ResponseEntity<?> buscarMedico(@RequestBody DtoBuscarPorId dato) {
            var medico = medicoService.buscarMedico(dato.id());
            if (medico == null) {
                throw new EntidadNoEncontradaException("Médico no encontrado");
            }
            return ResponseEntity.ok(medico);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<?> modificaMedico(@RequestBody DtoModificaMedico dato) {
            var medico = medicoService.modificaMedico(dato);
            if (medico == null) {
                throw new EntidadNoEncontradaException("Médico no encontrado");
            }
            return ResponseEntity.ok(medico);
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity<?> eliminarMedico(@RequestBody DtoBuscarPorId dato) {
        Boolean medico = medicoService.eliminarMedico(dato.id());
        if (!medico) {
            throw new EntidadNoEncontradaException("Médico no encontrado");
        }
        return ResponseEntity.ok("Médico eliminado Correctamente");
    }
}
