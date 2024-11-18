package com.consultorio.oftalmologico.application.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.consultorio.oftalmologico.domain.entities.clinica.Clinica;
import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;
import com.consultorio.oftalmologico.domain.enums.UserRole;
import com.consultorio.oftalmologico.domain.repository.ClinicaRepository;
import com.consultorio.oftalmologico.domain.repository.RegistroActividadRepository;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.EntidadNoEncontradaException;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.ObjectAlreadyExistsException;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.RelacionNoValidaException;
import com.consultorio.oftalmologico.presentation.dto.clinica.DtoRegistroClinica;
import com.consultorio.oftalmologico.presentation.dto.clinica.DtoRespuestaClinica;
import com.consultorio.oftalmologico.presentation.dto.registroActividad.DtoRegistroActividad;

import jakarta.validation.Valid;

@Service
public class ClinicaService {

    private final ClinicaRepository clinicaRepository;

    private final RegistroActividadService registroActividadService;

    public ClinicaService(ClinicaRepository clinicaRepository, RegistroActividadRepository registroActividadRepository, RegistroActividadService registroActividadService) {
        this.clinicaRepository = clinicaRepository;
        this.registroActividadService = registroActividadService;
    }

    public DtoRespuestaClinica crearClinica(@Valid DtoRegistroClinica dato, Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        if (usuarioLogueado.getRole() != UserRole.ADMIN) {
            throw new RelacionNoValidaException("No tienes permiso para realizar esta acción");
        }
        if (clinicaRepository.findByNombre(dato.nombre()) != null ) {
            throw new ObjectAlreadyExistsException("El nombre de la clínica ya está en uso");
        }

        Clinica clinica = new Clinica();
        clinica.setNombre(dato.nombre());
        clinica.setDomicilio(dato.domicilio());
        clinica.setIdentificacionFiscal(dato.identificacionFiscal());
        clinica.setActivo(dato.activo());
        clinica.setPais(dato.pais());
        clinicaRepository.save(clinica);

        // Registrar la actividad
        registroActividadService.registroActividad(new DtoRegistroActividad(
                null, usuarioLogueado.getId(), "CREAR",
                "Se registró una nueva clínica: " + clinica.getId()
        ));

        return new DtoRespuestaClinica(clinica);
    }

    public Page<DtoRespuestaClinica> listarClinica(Pageable pageable, Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        if (usuarioLogueado.getRole() != UserRole.ADMIN) {
            throw new RelacionNoValidaException("No tines permiso para realizar esta acción");
        }

        Page<Clinica> clinicas = clinicaRepository.findAllOrderByNombre(pageable);

        List<DtoRespuestaClinica> dtoList = clinicas.getContent().stream()
                .map(DtoRespuestaClinica::new)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList);
    }

    private Clinica obtenerClinicaPorId(Long id) {
        var clinica = clinicaRepository.findByIdAndTrue(id);
        if (clinica == null) {
            throw new EntidadNoEncontradaException("Clínica no encontrada");
        }
        return clinica;
    }

    private Clinica obtenerClinicaPorIdentificacionFiscal(String identificacionFiscal) {
        var clinica = clinicaRepository.findByIdentificacionFiscal(identificacionFiscal);
        if (clinica == null) {
            throw new EntidadNoEncontradaException("Clínica no encontrada");
        }
        return clinica;
    }

    public DtoRespuestaClinica buscarClinica(Long id, Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        if (usuarioLogueado.getRole() != UserRole.ADMIN) {
            throw new RelacionNoValidaException("No tines permiso para realizar esta acción");
        }

        var clinica = obtenerClinicaPorId(id);
        return new DtoRespuestaClinica(clinica);
    }

    public DtoRespuestaClinica modificaClinica(DtoRegistroClinica dato, Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        if (usuarioLogueado.getRole() != UserRole.ADMIN) {
            throw new RelacionNoValidaException("No tines permiso para realizar esta acción");
        }

        var clinica = obtenerClinicaPorIdentificacionFiscal(dato.identificacionFiscal());
        
        if (dato.nombre() != null) {
            clinica.setNombre(dato.nombre());
        }
        if (dato.domicilio() != null) {
            clinica.setDomicilio(dato.domicilio());
        }
        if (dato.identificacionFiscal() != null) {
            clinica.setIdentificacionFiscal(dato.identificacionFiscal());
        }
        if (dato.pais() != null){
            clinica.setPais(dato.pais());
        }
        
        clinicaRepository.save(clinica);

        // Registrar la actividad
        registroActividadService.registroActividad(new DtoRegistroActividad(
                null, usuarioLogueado.getId(), "MODIFICAR",
                "Se modificó la clínica: " + clinica.getId()
        ));

        return new DtoRespuestaClinica(clinica);
    }

    public DtoRespuestaClinica buscarClinicaPorCuit(String identificacionFiscal, Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        if (usuarioLogueado.getRole() != UserRole.ADMIN) {
            throw new RelacionNoValidaException("No tines permiso para realizar esta acción");
        }

        var clinica = clinicaRepository.findByIdentificacionFiscal(identificacionFiscal);
        if (clinica == null) {
            throw new EntidadNoEncontradaException("Clínica no encontrada");
        }
        return new DtoRespuestaClinica(clinica);
    }

    public DtoRespuestaClinica buscarPorNombre(String nombre, Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        if (usuarioLogueado.getRole() != UserRole.ADMIN) {
            throw new RelacionNoValidaException("No tines permiso para realizar esta acción");
        }

        var clinica = clinicaRepository.findByNombre(nombre);
        if (clinica == null) {
            throw new EntidadNoEncontradaException("Clínica no encontrada");
        }
        return  new DtoRespuestaClinica(clinica);
    }

    public Boolean eliminarClinica(Long id, Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        if (usuarioLogueado.getRole() != UserRole.ADMIN) {
            throw new RelacionNoValidaException("No tines permiso para realizar esta acción");
        }

        var clinica = clinicaRepository.findByIdAndTrue(id);
        if (clinica == null || !clinica.getActivo()) {
            return false;
        }
        clinica.setActivo(false);
        clinicaRepository.save(clinica);

        // Registrar la actividad
        registroActividadService.registroActividad(new DtoRegistroActividad(
                null, usuarioLogueado.getId(), "ELIMINAR",
                "Se eliminó la clínica: " + clinica.getId()
        ));

        return true;
    }

    public Boolean restaurarClinica(String identificaionFiscal, Authentication authentication) {
        var usuarioLogueado = (Usuario) authentication.getPrincipal();
        if (usuarioLogueado.getRole() != UserRole.ADMIN) {
            throw new RelacionNoValidaException("No tines permiso para realizar esta acción");
        }

        var clinica = clinicaRepository.findByIdentificacionFiscal(identificaionFiscal);
        if (clinica == null || clinica.getActivo()) {
            return false;
        }
        clinica.setActivo(true);
        clinicaRepository.save(clinica);

        // Registrar la actividad
        registroActividadService.registroActividad(new DtoRegistroActividad(
                null, usuarioLogueado.getId(), "RESTAURAR",
                "Se restauró la clínica: " + clinica.getId()
        ));

        return true;
    }
}
