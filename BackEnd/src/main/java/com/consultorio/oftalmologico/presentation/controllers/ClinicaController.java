package com.consultorio.oftalmologico.presentation.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.consultorio.oftalmologico.application.services.ClinicaService;
import com.consultorio.oftalmologico.infraestructure.errors.errorsDto.DtoRespuestaErrores;
import com.consultorio.oftalmologico.presentation.dto.DtoBuscarPorId;
import com.consultorio.oftalmologico.presentation.dto.clinica.DtoIdentificacionFiscal;
import com.consultorio.oftalmologico.presentation.dto.clinica.DtoRazonSocial;
import com.consultorio.oftalmologico.presentation.dto.clinica.DtoRegistroClinica;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clinica")
public class ClinicaController {

    private final ClinicaService clinicaService;

    public ClinicaController(ClinicaService clinicaService) {
        this.clinicaService = clinicaService;
    }

    /**
     * Crea una nueva clínica.
     * 
     * @param dato          Datos de la nueva clínica.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con la clínica creada.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> crearClinica(@RequestBody @Valid DtoRegistroClinica dato, Authentication authentication) {
        var clinica = clinicaService.crearClinica(dato, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(clinica);
    }

    /**
     * Lista todas las clínicas.
     * 
     * @param pageable      Información de paginación.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con la lista de clínicas.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarClinicas(Pageable pageable, Authentication authentication) {
        var clinicas = clinicaService.listarClinica(pageable, authentication);
        return ResponseEntity.ok(clinicas.getContent());
    }

    /**
     * Busca una clínica por ID.
     * 
     * @param dato          Datos de búsqueda.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con la clínica encontrada o un error.
     */
    @PostMapping("/find")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> buscarPorId(@RequestBody DtoBuscarPorId dato, Authentication authentication) {
        var clinica = clinicaService.buscarClinica(dato.id(), authentication);
        if (clinica == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(), "Clínica no encontrada"));
        }
        return ResponseEntity.ok(clinica);
    }

    /**
     * Busca una clínica por CUIT.
     * 
     * @param dato          Datos de búsqueda.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con la clínica encontrada o un error.
     */
    @PostMapping("/find/by-cuit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> buscarPorCuit(@RequestBody DtoIdentificacionFiscal dato, Authentication authentication) {
        var clinica = clinicaService.buscarClinicaPorCuit(dato.identificacionFiscal(), authentication);
        if (clinica == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(), "Clínica no encontrada"));
        }
        return ResponseEntity.ok(clinica);
    }

    /**
     * Busca una clínica por razón social.
     * 
     * @param dato          Datos de búsqueda.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con la clínica encontrada o un error.
     */
    @PostMapping("/find/by-name")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> buscarPorRazonSocial(@RequestBody DtoRazonSocial dato, Authentication authentication) {
        var clinica = clinicaService.buscarPorNombre(dato.nombre(), authentication);
        if (clinica == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(), "Clínica no encontrada"));
        }
        return ResponseEntity.ok(clinica);
    }

    /**
     * Modifica una clínica existente.
     * 
     * @param dato          Datos de la clínica a modificar.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con la clínica modificada.
     */
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> modificarClinica(@RequestBody DtoRegistroClinica dato, Authentication authentication) {
        var clinica = clinicaService.modificaClinica(dato, authentication);
        return ResponseEntity.ok(clinica);
    }

    /**
     * Elimina una clínica.
     * 
     * @param dato          Datos de la clínica a eliminar.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con el resultado de la eliminación.
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> eliminarClinica(@RequestBody DtoBuscarPorId dato, Authentication authentication) {
        Boolean eliminado = clinicaService.eliminarClinica(dato.id(), authentication);
        if (eliminado) {
            return ResponseEntity.ok(new DtoRespuestaErrores(HttpStatus.OK.toString(), "Clínica eliminada correctamente"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(), "Clínica no encontrada"));
    }

    /**
     * Restaura una clínica eliminada.
     * 
     * @param dato          Datos de la clínica a restaurar.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con el resultado de la restauración.
     */
    @PutMapping("/restore")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> recuperarClinica(@RequestBody DtoIdentificacionFiscal dato, Authentication authentication) {
        Boolean recuperado = clinicaService.restaurarClinica(dato.identificacionFiscal(), authentication);
        if (recuperado) {
            return ResponseEntity.ok(new DtoRespuestaErrores(HttpStatus.OK.toString(), "Clínica restaurada exitosamente"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(), "Clínica no encontrada o actualmente en actividad"));
    }
}
