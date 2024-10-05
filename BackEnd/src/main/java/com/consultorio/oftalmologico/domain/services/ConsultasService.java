package com.consultorio.oftalmologico.domain.services;

import com.consultorio.oftalmologico.domain.dto.consulta.DtoConsultaDiaria;
import com.consultorio.oftalmologico.domain.dto.consulta.DtoModificaConsulta;
import com.consultorio.oftalmologico.domain.dto.consulta.DtoNuevaConsulta;
import com.consultorio.oftalmologico.domain.dto.consulta.DtoRespuestaConsulta;
import com.consultorio.oftalmologico.domain.dto.paciente.DtoRespuestaPaciente;
import com.consultorio.oftalmologico.domain.entities.historiaclinica.HistoriaClinica;
import com.consultorio.oftalmologico.domain.entities.paciente.Paciente;
import com.consultorio.oftalmologico.domain.repository.ConsultaRepository;
import com.consultorio.oftalmologico.domain.repository.MedicoRepository;
import com.consultorio.oftalmologico.domain.repository.PacienteRepository;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.EntidadNoEncontradaException;
import com.consultorio.oftalmologico.infraestructure.errors.exceptions.ObjectAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultasService {
    @Autowired
    ConsultaRepository consultaRepository;

    @Autowired
    PacienteRepository pacienteRepository;

    @Autowired
    MedicoRepository medicoRepository;


    public DtoRespuestaConsulta guardarConsulta(DtoNuevaConsulta dato) {
        var medico = medicoRepository.findByIdAndActivoFalse(dato.medicoId());

        var paciente = pacienteRepository.findByDniAndActivoNotFalse(dato.pacienteDni());
        if (paciente == null) {
            throw new EntidadNoEncontradaException("Paciente inexistente");
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
        historiaClinica.setPacienteDni(dato.pacienteDni());
        historiaClinica.setMedicoId(dato.medicoId());
        historiaClinica.setActivo(true);
        consultaRepository.save(historiaClinica);
        return new DtoRespuestaConsulta(historiaClinica);
    }

    public DtoRespuestaConsulta modificaConstulta(DtoModificaConsulta dato) {
        var consulta = consultaRepository.findByIdAndActivo(dato.id());
        if (consulta == null){
            throw new ObjectAlreadyExistsException("Consulta inexistente");
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

    public Page<DtoRespuestaConsulta> consultar(Pageable page) {
        return consultaRepository.findAll(page).map(DtoRespuestaConsulta::new);
    }

    public DtoRespuestaConsulta buscarPacienteId(Long id) {
        var consulta = consultaRepository.findByIdAndActivo(id);
        if (consulta == null) {
            throw new ObjectAlreadyExistsException("Consulta inexistente");
        }
        return new DtoRespuestaConsulta(consulta);
    }

    public Page<DtoRespuestaConsulta> listarPorPaciente(Long pacienteDni, Pageable pageable) {
        var consulta = consultaRepository.findByPacienteDniAndActivo(pacienteDni, pageable);
        if (consulta.isEmpty() ){
            throw new EntidadNoEncontradaException("Consulta inexistente");
        }
        List<DtoRespuestaConsulta> dtoList = consulta.getContent().stream()
                .map(c -> new DtoRespuestaConsulta(c))
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList);
    }

    public Page<DtoConsultaDiaria> listarPorFecha(LocalDate fecha, Long medicoId, Pageable pageable) {
        var consulta = consultaRepository.findAllByFechaConsulta(fecha, medicoId, pageable);
        if (consulta.isEmpty()) {
            throw new EntidadNoEncontradaException("No se encontró consulta para la fecha solicitada");
        }

        return consulta.map(c -> {
                    Paciente paciente = pacienteRepository.findByDniAndActivoNotFalse(c.getPacienteDni());
                    return new DtoConsultaDiaria(c, paciente);
                }
        );
    }
}
