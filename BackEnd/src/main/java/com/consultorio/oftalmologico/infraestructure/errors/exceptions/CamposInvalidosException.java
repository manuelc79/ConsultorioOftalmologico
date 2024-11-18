package com.consultorio.oftalmologico.infraestructure.errors.exceptions;

public class CamposInvalidosException extends RuntimeException {
    public CamposInvalidosException(String message) {
        super(message);
    }
}
