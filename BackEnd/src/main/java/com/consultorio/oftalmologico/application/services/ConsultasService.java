package com.consultorio.oftalmologico.application.services;

import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;
import com.consultorio.oftalmologico.presentation.dto.DtoBuscaPorFecha;
import com.consultorio.oftalmologico.presentation.dto.consulta.DtoConsultaDiaria;
import com.consultorio.oftalmologico.presentation.dto.consulta.DtoModificaConsulta;
import com.consultorio.oftalmologico.presentation.dto.consulta.DtoNuevaConsulta;
import com.consultorio.oftalmologico.presentation.dto.consulta.DtoRespuestaConsulta;
import com.consultorio.oftalmologico.domain.entities.historiaclinica.HistoriaClinica;
import com.consultorio.oftalmologico.domain.entities.paciente.Paciente;
import com.consultorio.oftalmologico.domain.repository.ConsultaRepository;
import com.consultorio.oftalmologico.domain.repository.UsuarioRepository;
import com.consultorio.oftalmologico.domain.repository.PacienteRepository;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.EntidadNoEncontradaException;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.ObjectAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ConsultasService {
    @Autowired
    ConsultaRepository consultaRepository;

    @Autowired
    PacienteRepository pacienteRepository;

    @Autowired
    UsuarioRepository usuarioRepository;


    public DtoRespuestaConsulta guardarConsulta(DtoNuevaConsulta dato, Authentication authentication) throws AccessDeniedException {
        var medico = (Usuario) authentication.getPrincipal();

        var paciente = pacienteRepository.findByDniAndActivo(dato.pacienteDni());
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
        return new DtoRespuestaConsulta(historiaClinica);
    }

    public DtoRespuestaConsulta modificaConstulta(DtoModificaConsulta dato, Authentication authentication) throws AccessDeniedException {
        var medico = (Usuario) authentication.getPrincipal();
        var consulta = consultaRepository.findByIdAndActivo(dato.id(), medico.getClinica().getId());
        if (consulta == null){
            throw new ObjectAlreadyExistsException("Consulta inexistente");
        }
        if (consulta.getClinica().getId() != medico.getClinica().getId()) {
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
                .map(paciente -> new DtoRespuestaConsulta(paciente))
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
                    Paciente paciente = pacienteRepository.findByDniAndActivo(c.getPaciente().getDni());
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
        return true;
    }
}
