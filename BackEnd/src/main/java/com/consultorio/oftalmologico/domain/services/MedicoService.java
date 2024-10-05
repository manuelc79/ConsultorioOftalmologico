package com.consultorio.oftalmologico.domain.services;

import com.consultorio.oftalmologico.domain.dto.medico.DtoAutenticarMedico;
import com.consultorio.oftalmologico.domain.dto.medico.DtoModificaMedico;
import com.consultorio.oftalmologico.domain.dto.medico.DtoRegistroMedico;
import com.consultorio.oftalmologico.domain.dto.medico.DtoRespuestaMedico;
import com.consultorio.oftalmologico.domain.entities.medico.DetallesMedico;
import com.consultorio.oftalmologico.domain.entities.medico.Medico;
import com.consultorio.oftalmologico.domain.repository.DetallesMedicoRepository;
import com.consultorio.oftalmologico.domain.repository.MedicoRepository;
import com.consultorio.oftalmologico.domain.validations.ValidationMedico;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.EntidadNoEncontradaException;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.ObjectAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MedicoService {
    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private DetallesMedicoRepository detallesMedicoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public DtoRespuestaMedico registrarMedico(DtoRegistroMedico dato) {
        String errores = ValidationMedico.validarCamposEnBlanco(dato);
        if (!errores.equals("{}")) {
            throw new IllegalArgumentException("Campos inválidos" + errores);
        }
        if (medicoRepository.findByEmail(dato.email()) != null) {
            throw new ObjectAlreadyExistsException("El email ya está en uso");
        }

        Medico medico = new Medico();
        medico.setEmail(dato.email());
        medico.setPassword(passwordEncoder.encode(dato.password()));
        medico.setActivo(true);
        medico = medicoRepository.save(medico);

        DetallesMedico detallesMedico = new DetallesMedico();
        detallesMedico.setApellido(dato.apellido());
        detallesMedico.setNombre(dato.nombre());
        detallesMedico.setEspecialidad(dato.especialidad());
        detallesMedico.setNumeroMatricula(dato.numeroMatricula());
        detallesMedico.setTelefono(dato.telefono());
        detallesMedico.setMedico(medico);
        detallesMedicoRepository.save(detallesMedico);

        return new DtoRespuestaMedico(medico, detallesMedico);
    }

    public DtoRespuestaMedico buscarMedico(Long id) {
        var medico = medicoRepository.findByIdAndActivoFalse(id);
        if (medico == null ){
            throw new EntidadNoEncontradaException("Medico no Encontrado");
        }
        DetallesMedico detallesMedico = detallesMedicoRepository.findByMedicoId(id);
        return new DtoRespuestaMedico(medico, detallesMedico);
    }

    public DtoRespuestaMedico modificaMedico(DtoModificaMedico dato) {
        var medico = medicoRepository.findByIdAndActivoFalse(dato.id());
        if (medico == null) {
            throw new EntidadNoEncontradaException("Medico no encontrado");
        }
        DetallesMedico detallesMedico = detallesMedicoRepository.findByMedicoId(medico.getId());
        if (dato.password() != null) {
            medico.setPassword(passwordEncoder.encode(dato.password()));
        }
        if (dato.nombre() != null){
            detallesMedico.setNombre(dato.nombre());
        }
        if (dato.apellido() != null){
            detallesMedico.setApellido(dato.apellido());
        }
        if (dato.especialidad() != null) {
            detallesMedico.setEspecialidad(dato.especialidad());
        }
        if (dato.numeroMatricula() != null) {
            detallesMedico.setNumeroMatricula(dato.numeroMatricula());
        }
        if (dato.telefono() != null) {
            detallesMedico.setTelefono(dato.telefono());
        }
        medicoRepository.save(medico);
        detallesMedicoRepository.save(detallesMedico);
        return new DtoRespuestaMedico(medico, detallesMedico);
    }

    public Boolean eliminarMedico(Long id) {
        var medico = medicoRepository.findByIdAndActivoFalse(id);
        if (medico == null) {
            return false;
        }
        medico.setActivo(false);
        medicoRepository.save(medico);
        return true;
    }
}
