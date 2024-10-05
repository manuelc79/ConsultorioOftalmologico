package com.consultorio.oftalmologico.infraestructure.errors;

import com.consultorio.oftalmologico.infraestructure.errors.errorsDto.DtoRespuestaErrores;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.EntidadNoEncontradaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DtoRespuestaErrores> handleGlobalException(Exception ex) {
        DtoRespuestaErrores errores = new DtoRespuestaErrores(
                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                "Ocurrió un error inesperado: " + ex.getMessage()
        );
        return new ResponseEntity<>(errores, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<DtoRespuestaErrores> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> detallesErrores = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage()
                ));
        var errores = new DtoRespuestaErrores(
                HttpStatus.BAD_REQUEST.toString(),
                "Error de validación",
                detallesErrores
        );
        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntidadNoEncontradaException.class)
    public ResponseEntity<DtoRespuestaErrores> handleEntityNotFoundException(EntidadNoEncontradaException ex) {
        DtoRespuestaErrores errores = new DtoRespuestaErrores(
                HttpStatus.NOT_FOUND.toString(),
                ex.getMessage()
        );
        return new ResponseEntity<>(errores, HttpStatus.NOT_FOUND);
    }
}
