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
        
        var errores = new DtoRespuestaErrores(
                HttpStatus.BAD_REQUEST.toString(),
                mensaje
        );
        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<DtoRespuestaErrores> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        var errores = new DtoRespuestaErrores(
                HttpStatus.BAD_REQUEST.toString(),
                "Error de integridad de datos: Verifica que todas las relaciones necesarias existan"
        );
        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RelacionNoValidaException.class)
    public ResponseEntity<DtoRespuestaErrores> handleRelacionNoValidaException(RelacionNoValidaException ex) {
        var errores = new DtoRespuestaErrores(
                HttpStatus.BAD_REQUEST.toString(),
                ex.getMessage()
        );
        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<DtoRespuestaErrores> handleAccessDeniedException(AccessDeniedException ex) {
        var errores = new DtoRespuestaErrores(
                HttpStatus.FORBIDDEN.toString(),
                "Acceso denegado: No tiene los permisos necesarios"
        );
        return new ResponseEntity<>(errores, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<DtoRespuestaErrores> handleIllegalArgumentException(IllegalArgumentException ex) {
        var errores = new DtoRespuestaErrores(
                HttpStatus.BAD_REQUEST.toString(),
                ex.getMessage()
        );
        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntidadNoEncontradaException.class)
    public ResponseEntity<DtoRespuestaErrores> handleEntidadNoEncontradaException(EntidadNoEncontradaException ex) {
        var errores = new DtoRespuestaErrores(
                HttpStatus.NOT_FOUND.toString(),
                ex.getMessage()
        );
        return new ResponseEntity<>(errores, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ObjectAlreadyExistsException.class)
    public ResponseEntity<DtoRespuestaErrores> handleObjectAlreadyExistsException(ObjectAlreadyExistsException ex) {
        var errores = new DtoRespuestaErrores(
                HttpStatus.BAD_REQUEST.toString(),
                ex.getMessage()
        );
        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DtoRespuestaErrores> handleGenericException(Exception ex) {
        var errores = new DtoRespuestaErrores(
                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                "Error interno del servidor: " + ex.getMessage()
        );
        return new ResponseEntity<>(errores, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
