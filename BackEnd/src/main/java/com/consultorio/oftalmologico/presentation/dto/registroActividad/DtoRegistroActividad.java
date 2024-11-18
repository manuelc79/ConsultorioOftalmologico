package com.consultorio.oftalmologico.presentation.dto.registroActividad;

public record DtoRegistroActividad(
        Long id,
        Long usuarioId,
        String tipoOperacion,
        String descripcion) {
}
