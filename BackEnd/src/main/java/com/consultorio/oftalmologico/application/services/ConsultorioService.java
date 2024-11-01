package com.consultorio.oftalmologico.application.services;

import com.consultorio.oftalmologico.presentation.dto.consultorio.DtoModificaConsultorio;
import com.consultorio.oftalmologico.presentation.dto.consultorio.DtoRegistroConsultorio;
import com.consultorio.oftalmologico.presentation.dto.consultorio.DtoRespuestaConsultorio;
import com.consultorio.oftalmologico.domain.entities.consultorio.Consultorio;
import com.consultorio.oftalmologico.domain.repository.ConsultorioRepository;
import com.consultorio.oftalmologico.domain.repository.UsuarioRepository;
import com.consultorio.oftalmologico.domain.repository.ClinicaRepository;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.EntidadNoEncontradaException;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.ObjectAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConsultorioService {

    @Autowired
    private ConsultorioRepository consultorioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClinicaRepository clinicaRepository;

    public DtoRespuestaConsultorio crearConsultorio(DtoRegistroConsultorio dato, Long clinicaId) {
        var consultorio = consultorioRepository.findByUsuarioId(dato.usuarioId());
        if (consultorio != null) {
            throw new ObjectAlreadyExistsException("Este medico ya tiene asignado un consultorio");
        }
        var usuario = usuarioRepository.findByIdAndActivo(dato.usuarioId(), clinicaId);
        if (usuario == null) {
            throw new EntidadNoEncontradaException("Medico inexistente");
        }
        Consultorio nuevoConsultorio = new Consultorio();
        nuevoConsultorio.setDomicilio(dato.domicilio());
        nuevoConsultorio.setTelefono(dato.telefono());
        nuevoConsultorio.setLocalidad(dato.localidad());
        nuevoConsultorio.setLogo(dato.logo());
        nuevoConsultorio.setUsuario(usuario);
        consultorioRepository.save(nuevoConsultorio);
        return new DtoRespuestaConsultorio(nuevoConsultorio);
    }

    public DtoRespuestaConsultorio buscarConsultorio(Long id) {
        var consultorio = consultorioRepository.findByUsuarioId(id);
        if (consultorio == null) {
            throw new EntidadNoEncontradaException("Consultorio Inexistente");
        }
        return new DtoRespuestaConsultorio(consultorio);
    }

    public DtoRespuestaConsultorio modificaConsultorio(DtoModificaConsultorio dato) {
        var consultorio = consultorioRepository.buscarPorId(dato.id());
        if (consultorio == null) {
            throw new EntidadNoEncontradaException("Consultorio inexistente");
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
        if (dato.usuarioId() != null) {
            var usuario = usuarioRepository.findByIdAndActivo(dato.usuarioId(), dato.clinicaId());
            if (usuario == null) {
                throw new EntidadNoEncontradaException("Médico inexistente");
            }
            consultorio.setUsuario(usuario);
        }
        if (dato.clinicaId() != null) {
            var clinica = clinicaRepository.findById(dato.clinicaId());
            if (clinica.isEmpty()) {
                throw new EntidadNoEncontradaException("Clínica inexistente");
            }
            consultorio.setClinica(clinica.get());
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
