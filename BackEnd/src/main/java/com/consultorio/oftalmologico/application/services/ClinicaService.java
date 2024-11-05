package com.consultorio.oftalmologico.application.services;

import com.consultorio.oftalmologico.infraestructure.errors.errorsDto.DtoRespuestaErrores;
import com.consultorio.oftalmologico.presentation.dto.clinica.DtoRegistroClinica;
import com.consultorio.oftalmologico.presentation.dto.clinica.DtoRespuestaClinica;
import com.consultorio.oftalmologico.domain.entities.clinica.Clinica;
import com.consultorio.oftalmologico.domain.repository.ClinicaRepository;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.EntidadNoEncontradaException;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.ObjectAlreadyExistsException;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClinicaService {
    @Autowired
    private ClinicaRepository clinicaRepository;

    public DtoRespuestaClinica crearClinica(@Valid DtoRegistroClinica dato) {
        if (clinicaRepository.findByNombre(dato.nombre()) != null ) {
            throw new ObjectAlreadyExistsException("El nombre de la clínica ya está en uso");
        }

        Clinica clinica = new Clinica();
        clinica.setNombre(dato.nombre());
        clinica.setDomicilio(dato.domicilio());
        clinica.setIdentificacionFiscal(dato.identificacionFiscal());
        clinica.setActivo(dato.activo());
        clinicaRepository.save(clinica);

        return new DtoRespuestaClinica(clinica);
    }

    public Page<DtoRespuestaClinica> listarClinica(Pageable pageable) {
        Page<Clinica> clinicas = clinicaRepository.findAllOrderByNombre(pageable);

        List<DtoRespuestaClinica> dtoList = clinicas.getContent().stream()
                .map(DtoRespuestaClinica::new)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList);
    }

    public DtoRespuestaClinica buscarClinica(Long id) {
        var clinica = clinicaRepository.findByIdAndTrue(id);
        if (clinica == null){
            throw new EntidadNoEncontradaException("Clinica no encontrada");
        }
        return new DtoRespuestaClinica(clinica);
    }

    public DtoRespuestaClinica modificaClinica(DtoRegistroClinica dato) {
        var clinica = clinicaRepository.findByIdentificacionFiscal(dato.identificacionFiscal());
        if (clinica == null) {
            throw new EntidadNoEncontradaException("Clinica no encontrada");
        }
        if (dato.nombre() != null) {
            clinica.setNombre(dato.nombre());
        }
        if (dato.domicilio() != null) {
            clinica.setDomicilio(dato.domicilio());
        }
        if (dato.identificacionFiscal() != null) {
            clinica.setIdentificacionFiscal(dato.identificacionFiscal());
        }
        clinicaRepository.save(clinica);
        return new DtoRespuestaClinica(clinica);
    }

    public DtoRespuestaClinica buscarClinicaPorCuit(String identificacionFiscal) {
        var clinica = clinicaRepository.findByIdentificacionFiscal(identificacionFiscal);
        if (clinica == null) {
            throw new EntidadNoEncontradaException("Clinica no encontrada");
        }
        return new DtoRespuestaClinica(clinica);
    }

    public DtoRespuestaClinica buscarPorNombre(String nombre) {
        var clinica = clinicaRepository.findByNombre(nombre);
        if (clinica == null) {
            throw new EntidadNoEncontradaException("Clinica no Encontrada");
        }
        return  new DtoRespuestaClinica(clinica);
    }

    public Boolean eliminarClinica(Long id) {
        var clinica = clinicaRepository.findByIdAndTrue(id);
        if (clinica == null || !clinica.getActivo()) {
            return false;
        }
        clinica.setActivo(false);
        clinicaRepository.save(clinica);
        return true;
    }

    public Boolean restaurarClinica(String identificaionFiscal) {
        var clinica = clinicaRepository.findByIdentificacionFiscal(identificaionFiscal);
        if (clinica == null || clinica.getActivo()) {
            return false;
        }
        clinica.setActivo(true);
        clinicaRepository.save(clinica);
        return true;
    }
}
