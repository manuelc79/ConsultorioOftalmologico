package com.consultorio.oftalmologico.infraestructure.errors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.consultorio.oftalmologico.infraestructure.errors.errorsDto.DtoRespuestaErrores;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.EntidadNoEncontradaException;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.ObjectAlreadyExistsException;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.RelacionNoValidaException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnexpectedRollbackException.class)
    public ResponseEntity<DtoRespuestaErrores> handleUnexpectedRollbackException(UnexpectedRollbackException ex) {
        String mensaje = "Error en la transacción: ";
        if (ex.getCause() instanceof DataIntegrityViolationException) {
            mensaje += "Violación de integridad en la base de datos. Verifica que las relaciones sean válidas.";
        } else {
            mensaje += ex.getMostSpecificCause().getMessage();
        }
        return buildResponse(HttpStatus.BAD_REQUEST, mensaje);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<DtoRespuestaErrores> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Error de integridad de datos: Verifica que todas las relaciones necesarias existan");
    }

    @ExceptionHandler(RelacionNoValidaException.class)
    public ResponseEntity<DtoRespuestaErrores> handleRelacionNoValidaException(RelacionNoValidaException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<DtoRespuestaErrores> handleAccessDeniedException(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Acceso denegado: No tiene los permisos necesarios");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<DtoRespuestaErrores> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(EntidadNoEncontradaException.class)
    public ResponseEntity<DtoRespuestaErrores> handleEntidadNoEncontradaException(EntidadNoEncontradaException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ObjectAlreadyExistsException.class)
    public ResponseEntity<DtoRespuestaErrores> handleObjectAlreadyExistsException(ObjectAlreadyExistsException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DtoRespuestaErrores> handleGenericException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor: " + ex.getMessage());
    }

    private ResponseEntity<DtoRespuestaErrores> buildResponse(HttpStatus status, String message) {
        DtoRespuestaErrores errores = new DtoRespuestaErrores(status.toString(), message);
        return new ResponseEntity<>(errores, status);
    }
}
