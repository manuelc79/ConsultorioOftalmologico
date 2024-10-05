package com.consultorio.oftalmologico.infraestructure.errors.exceptions;

public class EntidadNoEncontradaException extends RuntimeException {
    public EntidadNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
