package com.consultorio.oftalmologico.infraestructure.errors.exceptions;

public class RelacionNoValidaException extends RuntimeException {
    public RelacionNoValidaException(String message) {
        super(message);
    }
}
