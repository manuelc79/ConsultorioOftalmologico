package com.consultorio.oftalmologico.presentation.dto.clinica;

public record DtoRegistroClinica(
        Long id,
        String nombre,
        String domicilio,
        String informacionFiscal,
        Boolean activo
       ) {
}
