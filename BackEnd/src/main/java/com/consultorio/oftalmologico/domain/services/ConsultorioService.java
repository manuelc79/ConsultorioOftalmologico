package com.consultorio.oftalmologico.domain.services;

import com.consultorio.oftalmologico.domain.dto.consultorio.DtoModificaConsultorio;
import com.consultorio.oftalmologico.domain.dto.consultorio.DtoRegistroConsultorio;
import com.consultorio.oftalmologico.domain.dto.consultorio.DtoRespuestaConsultorio;
import com.consultorio.oftalmologico.domain.entities.medico.Consultorio;
import com.consultorio.oftalmologico.domain.repository.ConsultorioRepository;
import com.consultorio.oftalmologico.domain.repository.MedicoRepository;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.EntidadNoEncontradaException;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.ObjectAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConsultorioService {

    @Autowired
    private ConsultorioRepository consultorioRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    public DtoRespuestaConsultorio crearConsultorio(DtoRegistroConsultorio dato) {
        var consultorio = consultorioRepository.findByMedicoId(dato.medicoId());
        if (consultorio != null) {
            throw new ObjectAlreadyExistsException("Este medico ya tiene asignado un consultorio");
        }
        var medico = medicoRepository.findByIdAndActivoFalse(dato.medicoId());
        if (medico == null) {
            throw new EntidadNoEncontradaException("Medico inexistente");
        }
        Consultorio nuevoConsultorio = new Consultorio();
        nuevoConsultorio.setDomicilio(dato.domicilio());
        nuevoConsultorio.setTelefono(dato.telefono());
        nuevoConsultorio.setLocalidad(dato.localidad());
        nuevoConsultorio.setLogo(dato.logo());
        nuevoConsultorio.setMedicoId(dato.medicoId());
        consultorioRepository.save(nuevoConsultorio);
        return new DtoRespuestaConsultorio(nuevoConsultorio);
    }

    public DtoRespuestaConsultorio buscarConsultorio(Long id) {
        var consultorio = consultorioRepository.findByMedicoId(id);
        if (consultorio == null) {
            throw new EntidadNoEncontradaException("Consultorio Inexistente");
        }
        return new DtoRespuestaConsultorio(consultorio);
    }

    public DtoRespuestaConsultorio modificaConsultorio(DtoModificaConsultorio dato) {
        var consultorio = consultorioRepository.buscarPorId(dato.id());
        if (consultorio == null) {
            throw new ObjectAlreadyExistsException("Consultorio inexistente");
        }
        if (dato.domicilio() != null) {
            consultorio.setDomicilio(dato.domicilio());
        }
        if (dato.telefono() != null) {
            consultorio.setTelefono(dato.telefono());
        }
        if (dato.localidad() != null) {
            consultorio.setLocalidad(dato.localidad());
        }
        if (dato.logo() != null) {
            consultorio.setLogo(dato.logo());
        }
        if (dato.medicoId() != null) {
            consultorio.setMedicoId(dato.medicoId());
        }
        consultorioRepository.save(consultorio);
        return new DtoRespuestaConsultorio(consultorio);
    }

    public Boolean eliminarConsultorio(Long id) {
        var consultorio = consultorioRepository.buscarPorId(id);
        if (consultorio == null) {
            return false;
        }
        consultorio.setActivo(false);
        return true;
    }
}
