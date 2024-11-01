package com.consultorio.oftalmologico.presentation.controllers;

import com.consultorio.oftalmologico.presentation.dto.DtoBuscarPorId;
import com.consultorio.oftalmologico.presentation.dto.clinica.DtoIdentificacionFiscal;
import com.consultorio.oftalmologico.presentation.dto.clinica.DtoRazonSocial;
import com.consultorio.oftalmologico.presentation.dto.clinica.DtoRegistroClinica;
import com.consultorio.oftalmologico.application.services.ClinicaService;
import com.consultorio.oftalmologico.infraestructure.errors.errorsDto.DtoRespuestaErrores;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/clinica")
public class ClinicaController {

    @Autowired
    private ClinicaService clinicaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> crearClinica(@RequestBody @Valid DtoRegistroClinica dato) {
        var clinica = clinicaService.crearClinica(dato);
        return ResponseEntity.status(HttpStatus.CREATED).body(clinica);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarClinicas(Pageable pageable) {
        var clinicas = clinicaService.listarClinica(pageable);
        return ResponseEntity.ok(clinicas.getContent());
    }

    @PostMapping("/find")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> buscarPorId(@RequestBody DtoBuscarPorId dato) {
        try {
            var clinica = clinicaService.buscarClinica(dato.id());
            return ResponseEntity.ok(clinica);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(),
                    "Clinica no Encontrada"));
        }
    }

    @PostMapping("/find/by-cuit") // buscar por Identificación Fiscal (CUIT)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> buscarPorCuit(@RequestBody DtoIdentificacionFiscal dato) {
        try {
            var clinica = clinicaService.buscarClinicaPorCuit(dato.identificacionFiscal());
            return ResponseEntity.ok(clinica);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(),
                            "Clinica no Encontrada"));
        }

    }

    @PostMapping("/find/by-name") // buscar por Razón Social (Nombre de la Clinica)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> buscarPorRazonSocial(@RequestBody DtoRazonSocial dato) {
        try {
            var clinica = clinicaService.buscarPorNombre(dato.nombre());
            return ResponseEntity.ok(clinica);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(),
                            "Clinica no Encontrada"));
        }

    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> modificarClinica(@RequestBody DtoRegistroClinica dato) {
        try {
            var clinica = clinicaService.modificaClinica(dato);
            return ResponseEntity.ok(clinica);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(),
                            "Clinica no encontrada"));
        }
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> elimiarClinica(@RequestBody DtoBuscarPorId dato) {
        Boolean eliminado = clinicaService.eliminarClinica(dato.id());
        if (eliminado) {
            return ResponseEntity.ok("Clinica eliminada correctamente");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DtoRespuestaErrores(
                HttpStatus.NOT_FOUND.toString(), "Clinica no encontrada"));
    }


}
