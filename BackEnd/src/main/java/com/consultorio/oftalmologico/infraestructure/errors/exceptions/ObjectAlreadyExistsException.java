package com.consultorio.oftalmologico.infraestructure.errors.exceptions;

public class ObjectAlreadyExistsException extends RuntimeException {

    public ObjectAlreadyExistsException(String message) {
        super(message);
    }
}
