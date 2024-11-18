package com.consultorio.oftalmologico.presentation.controllers;

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

    private final ConsultorioService consultorioService;

    public ConsultorioController(ConsultorioService consultorioService) {
        this.consultorioService = consultorioService;
    }

    /**
     * Crea un nuevo consultorio.
     * 
     * @param dato          Datos del nuevo consultorio.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con el consultorio creado.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> nuevoConsultorio(@RequestBody @Valid DtoRegistroConsultorio dato, Authentication authentication) {
        var consultorio = consultorioService.crearConsultorio(dato, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(consultorio);
    }

    /**
     * Busca un consultorio por ID de usuario.
     * 
     * @param dato          Datos de búsqueda.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con el consultorio encontrado o un error.
     */
    @PostMapping("/find")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'MEDICO')")
    public ResponseEntity<?> buscarConsultorio(@RequestBody DtoBuscarPorUsuarioId dato, Authentication authentication) {
        var consultorio = consultorioService.buscarConsultorio(dato.usuarioId(), authentication);
        if (consultorio == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(), "Consultorio no encontrado"));
        }
        return ResponseEntity.ok(consultorio);
    }

    /**
     * Modifica un consultorio existente.
     * 
     * @param dato          Datos del consultorio a modificar.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con el consultorio modificado.
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'MEDICO')")
    @Transactional
    public ResponseEntity<?> modificarConsultorio(@RequestBody DtoModificaConsultorio dato, Authentication authentication) {
        var consultorio = consultorioService.modificaConsultorio(dato, authentication);
        return ResponseEntity.ok(consultorio);
    }

    /**
     * Elimina un consultorio.
     * 
     * @param dato          Datos del consultorio a eliminar.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con el resultado de la eliminación.
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> eliminarConsultorio(@RequestBody DtoBuscarPorId dato, Authentication authentication) {
        boolean eliminado = consultorioService.eliminarConsultorio(dato.id(), authentication);
        if (eliminado) {
            return ResponseEntity.ok(new DtoRespuestaErrores(HttpStatus.OK.toString(), "Consultorio eliminado correctamente"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(), "Consultorio inexistente"));
    }

    /**
     * Restaura un consultorio eliminado.
     * 
     * @param dato          Datos del consultorio a restaurar.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con el resultado de la restauración.
     */
    @PutMapping("/restore")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<?> restaurarConsultorio(@RequestBody DtoBuscarPorId dato, Authentication authentication) {
        boolean restaurado = consultorioService.restaurarConsultorio(dato.id(), authentication);
        if (restaurado) {
            return ResponseEntity.ok(new DtoRespuestaErrores(HttpStatus.OK.toString(), "Consultorio restaurado correctamente"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DtoRespuestaErrores(HttpStatus.NOT_FOUND.toString(), "Consultorio inexistente o actualmente activo"));
    }

    /**
     * Lista todos los consultorios.
     * 
     * @param pageable      Información de paginación.
     * @param authentication Información de autenticación del usuario.
     * @return ResponseEntity con la lista de consultorios.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR')")
    public ResponseEntity<?> listarConsultorios(Pageable pageable, Authentication authentication) {
        var consultorios = consultorioService.listarConsultrios(pageable, authentication);
        return ResponseEntity.ok(consultorios.getContent());
    }
}
