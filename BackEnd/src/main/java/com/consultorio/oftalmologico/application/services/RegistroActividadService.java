package com.consultorio.oftalmologico.application.services;

import com.consultorio.oftalmologico.domain.entities.RegistroActividad;
import com.consultorio.oftalmologico.domain.repository.RegistroActividadRepository;
import com.consultorio.oftalmologico.presentation.dto.registroActividad.DtoRegistroActividad;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RegistroActividadService {

    private final RegistroActividadRepository registroActividadRepository;

    public RegistroActividadService(RegistroActividadRepository registroActividadRepository) {
        this.registroActividadRepository = registroActividadRepository;
    }

    public void registroActividad(DtoRegistroActividad dato) {
        RegistroActividad registro = new RegistroActividad();
        registro.setUsuarioId(dato.usuarioId());
        registro.setTipoOperacion(dato.tipoOperacion());
        registro.setDescripcion(dato.descripcion());
        registro.setFechaOperacion(LocalDateTime.now());
        registroActividadRepository.save(registro);
    }
}
