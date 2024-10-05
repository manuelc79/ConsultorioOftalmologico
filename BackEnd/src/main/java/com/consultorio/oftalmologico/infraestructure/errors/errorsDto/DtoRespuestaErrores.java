package com.consultorio.oftalmologico.infraestructure.errors.errorsDto;

import java.util.Map;

public record DtoRespuestaErrores(
        String codigo,
        String mensaje,
        Map<String, String> detallesErrores) {

    public DtoRespuestaErrores(String codigo, String mensaje) {
        this(codigo, mensaje, null); // Permite inicializar sin detallesErrores
    }
}
