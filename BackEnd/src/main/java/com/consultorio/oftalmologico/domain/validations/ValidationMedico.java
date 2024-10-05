package com.consultorio.oftalmologico.domain.validations;

import com.consultorio.oftalmologico.domain.dto.medico.DtoRegistroMedico;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class ValidationMedico {
    public static String validarCamposEnBlanco(DtoRegistroMedico dto) {
        Map<String, String> camposEnBlanco = new HashMap<>();

        if (dto.email() == null || dto.email().isBlank()) {
            camposEnBlanco.put("email", "El correo no debe estar en blanco");
        }
        if (dto.password() == null || dto.password().isBlank()) {
            camposEnBlanco.put("password", "La contraseña no debe estar en blanco");
        }
        if (dto.nombre() == null || dto.nombre().isBlank()){
            camposEnBlanco.put("nombre", "El nombre no debe estar en blanco");
        }
        if (dto.apellido() == null || dto.apellido().isBlank()) {
            camposEnBlanco.put("apellido", "El apellido no debe estar en blanco");
        }
        if (dto.especialidad() == null || dto.especialidad().isBlank()){
            camposEnBlanco.put("especialidad", "La especialidad no debe estar en blanco");
        }
        if (dto.numeroMatricula() == null) {
            camposEnBlanco.put("numeroMatricula", "La matricula no debe estar en blanco");
        }
        if (dto.telefono() == null){
            camposEnBlanco.put("telefono", "El teléfono no debe estar en blanco");
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(camposEnBlanco);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }
}
