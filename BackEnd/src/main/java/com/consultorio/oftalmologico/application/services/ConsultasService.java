package com.consultorio.oftalmologico.application.services;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.consultorio.oftalmologico.presentation.dto.registroActividad.DtoRegistroActividad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.consultorio.oftalmologico.domain.entities.RegistroActividad;
import com.consultorio.oftalmologico.domain.entities.historiaclinica.HistoriaClinica;
import com.consultorio.oftalmologico.domain.entities.paciente.Paciente;
import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;
import com.consultorio.oftalmologico.domain.repository.ConsultaRepository;
import com.consultorio.oftalmologico.domain.repository.PacienteRepository;
import com.consultorio.oftalmologico.domain.repository.RegistroActividadRepository;
import com.consultorio.oftalmologico.domain.repository.UsuarioRepository;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.EntidadNoEncontradaException;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.ObjectAlreadyExistsException;
import com.consultorio.oftalmologico.presentation.dto.DtoBuscaPorFecha;
import com.consultorio.oftalmologico.presentation.dto.consulta.DtoConsultaDiaria;
import com.consultorio.oftalmologico.presentation.dto.consulta.DtoModificaConsulta;
import com.consultorio.oftalmologico.presentation.dto.consulta.DtoNuevaConsulta;
import com.consultorio.oftalmologico.presentation.dto.consulta.DtoRespuestaConsulta;

@Service
public class ConsultasService {

    private final ConsultaRepository consultaRepository;

    private final PacienteRepository pacienteRepository;

    private final RegistroActividadService registroActividadService;

    public ConsultasService(ConsultaRepository consultaRepository, PacienteRepository pacienteRepository, UsuarioRepository usuarioRepository, RegistroActividadRepository registroActividadRepository, RegistroActividadService registroActividadService) {
        this.consultaRepository = consultaRepository;
        this.pacienteRepository = pacienteRepository;
        this.registroActividadService = registroActividadService;
    }

    public DtoRespuestaConsulta guardarConsulta(DtoNuevaConsulta dato, Authentication authentication) throws AccessDeniedException {
        var medico = (Usuario) authentication.getPrincipal();

        var paciente = pacienteRepository.findByDniAndActivo(dato.pacienteDni(), medico.getClinica().getPais());
        if (paciente == null) {
            throw new EntidadNoEncontradaException("Paciente inexistente");
        }
        if (!Objects.equals(paciente.getClinica().getId(), medico.getClinica().getId())) {
            throw new AccessDeniedException("No puedes realizar esta acción");
        }
        var consulta = consultaRepository.findByPacienteDniAndFechaConsulta(dato.pacienteDni(), LocalDate.now());
        if (consulta != null) {
            throw new ObjectAlreadyExistsException("Este paciente ya tuvo una consulta el día de hoy");
        }
        HistoriaClinica historiaClinica = new HistoriaClinica();
        historiaClinica.setFechaConsulta(LocalDate.now());
        historiaClinica.setAgudezaVisualOICC(dato.agudezaVisualOICC());
        historiaClinica.setAgudezaVisualODCC(dato.agudezaVisualODCC());
        historiaClinica.setAgudezaVisualOISC(dato.agudezaVisualOISC());
        historiaClinica.setAgudezaVisualODSC(dato.agudezaVisualODSC());
        historiaClinica.setLentesParaLejosOI(dato.lentesParaLejosOI());
        historiaClinica.setLentesParaLejosOD(dato.lentesParaLejosOD());
        historiaClinica.setLentesParaCercaAO(dato.lentesParaCercaAO());
        historiaClinica.setObservaciones(dato.observaciones());
        historiaClinica.setPaciente(paciente);
        historiaClinica.setUsuario(medico);
        historiaClinica.setActivo(true);
        historiaClinica.setClinica(medico.getClinica());
        consultaRepository.save(historiaClinica);

        // Registrar la actividad
        registroActividadService.registroActividad(new DtoRegistroActividad(
                null, medico.getId(), "CREAR",
                "Se registró una nueva consulta medica: " + historiaClinica.getId()
        ));

        return new DtoRespuestaConsulta(historiaClinica);
    }

    public DtoRespuestaConsulta modificaConsulta(DtoModificaConsulta dato, Authentication authentication) throws AccessDeniedException {
        var medico = (Usuario) authentication.getPrincipal();
        var consulta = consultaRepository.findByIdAndActivo(dato.id(), medico.getClinica().getId());
        if (consulta == null){
            throw new ObjectAlreadyExistsException("Consulta inexistente");
        }
        if (!Objects.equals(consulta.getClinica().getId(), medico.getClinica().getId())) {
            throw new AccessDeniedException("No puedes realizar esta acción");
        }
        if (dato.fechaConsulta() != null){
            consulta.setFechaConsulta(dato.fechaConsulta());
        }
        if (dato.agudezaVisualOICC()!= null){
            consulta.setAgudezaVisualOICC(dato.agudezaVisualOICC());
        }
        if (dato.agudezaVisualODCC() != null) {
            consulta.setAgudezaVisualODCC(dato.agudezaVisualODCC());
        }
        if (dato.agudezaVisualOISC()!= null){
            consulta.setAgudezaVisualOISC(dato.agudezaVisualOISC());
        }
        if (dato.agudezaVisualODSC() != null) {
            consulta.setAgudezaVisualODSC(dato.agudezaVisualODSC());
        }
        if (dato.lentesParaLejosOI() != null) {
            consulta.setLentesParaLejosOI(dato.lentesParaLejosOI());
        }
        if (dato.lentesParaLejosOD() != null) {
            consulta.setLentesParaLejosOD(dato.lentesParaLejosOD());
        }
        if (dato.lentesParaCercaAO() != null) {
            consulta.setLentesParaCercaAO(dato.lentesParaCercaAO());
        }
        if (dato.observaciones() != null) {
            consulta.setObservaciones(dato.observaciones());
        }
        consultaRepository.save(consulta);

        // Registrar la actividad
        registroActividadService.registroActividad(new DtoRegistroActividad(
                null, medico.getId(), "MODIFICAR",
                "Se modificó la consulta medica: " + consulta.getId()
        ));

        return new DtoRespuestaConsulta(consulta);
    }

    public Page<DtoRespuestaConsulta> consultar(Pageable page, Authentication authentication) {
        var medico = (Usuario) authentication.getPrincipal();

        return consultaRepository.findAllByConsultorio(page, medico.getClinica().getId()).map(DtoRespuestaConsulta::new);
    }

    public DtoRespuestaConsulta buscarConsultaId(Long id, Authentication authentication) {
        var medico = (Usuario) authentication.getPrincipal();

        var consulta = consultaRepository.findByIdAndActivo(id, medico.getClinica().getId());
        if (consulta == null) {
            throw new ObjectAlreadyExistsException("Consulta inexistente");
        }
        return new DtoRespuestaConsulta(consulta);
    }

    public Page<DtoRespuestaConsulta> listarPorPaciente(Long pacienteDni, Pageable pageable, Authentication authentication) {
        var medico = (Usuario) authentication.getPrincipal();
        var consulta = consultaRepository.findByPacienteDniAndActivo(pacienteDni, pageable, medico.getClinica().getId());
        if (consulta.isEmpty() ){
            throw new EntidadNoEncontradaException("Consulta inexistente");
        }
        List<DtoRespuestaConsulta> dtoList = consulta.getContent().stream()
                .map(DtoRespuestaConsulta::new)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList);
    }

    public Page<DtoConsultaDiaria> listarPorFecha(DtoBuscaPorFecha dato, Pageable pageable, Authentication authentication) {
        var medico = (Usuario) authentication.getPrincipal();
        var consulta = consultaRepository.findAllByFechaConsulta(dato.fechaConsulta(), medico.getId(), pageable, medico.getClinica().getId());
        if (consulta.isEmpty()) {
            throw new EntidadNoEncontradaException("No se encontró consulta para la fecha solicitada");
        }

        return consulta.map(c -> {
                    Paciente paciente = pacienteRepository.findByDniAndActivo(c.getPaciente().getDni(), medico.getClinica().getPais());
                    return new DtoConsultaDiaria(c, paciente);
                }
        );
    }

    public Boolean eliminarConsulta(Long id, Authentication authentication) {
        var medico = (Usuario) authentication.getPrincipal();
        var consulta = consultaRepository.findByIdAndActivo(id, medico.getClinica().getId() );
        if (consulta == null) {
            return false;
        }

        consultaRepository.delete(consulta);

        // Registrar la actividad
        registroActividadService.registroActividad(new DtoRegistroActividad(
                null, medico.getId(), "ELIMINAR",
                "Se eliminó la consulta medica: " + consulta.getId()
        ));

        return true;
    }
}
