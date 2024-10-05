package com.consultorio.oftalmologico.controllers;

import com.consultorio.oftalmologico.domain.dto.*;
import com.consultorio.oftalmologico.domain.dto.consulta.DtoModificaConsulta;
import com.consultorio.oftalmologico.domain.dto.consulta.DtoNuevaConsulta;
import com.consultorio.oftalmologico.domain.dto.consulta.DtoRespuestaConsulta;
import com.consultorio.oftalmologico.domain.dto.paciente.DtoBuscaPaciente;
import com.consultorio.oftalmologico.domain.services.ConsultasService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consulta")
public class ConsultasController {

    @Autowired
    ConsultasService consultasService;

    @PostMapping
    @Transactional
    public ResponseEntity<?> nuevaConsulta(@RequestBody @Valid DtoNuevaConsulta dato) {
        var consulta = consultasService.guardarConsulta(dato);
        return ResponseEntity.ok(consulta);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<?> modificaConsulta(@RequestBody DtoModificaConsulta dato) {
        var consulta = consultasService.modificaConstulta(dato);
        return ResponseEntity.ok(consulta);
    }

    @GetMapping
    public ResponseEntity listar(@PageableDefault(size = 10, sort = {"fechaConsulta"}) Pageable page) {
        var consultas = consultasService.consultar(page);
        return ResponseEntity.ok(consultas.getContent());
    }

    @GetMapping("/find")
    public ResponseEntity<?> mostrarPorPaciente (@RequestBody DtoBuscarPorId dato) {
        var consulta = consultasService.buscarPacienteId(dato.id());
        return ResponseEntity.ok(consulta);
    }

    @PostMapping("/find/paciente")
    public ResponseEntity listarPorPaciente(@PageableDefault(size = 10, sort = {"fechaConsulta"})
                                                                            @RequestBody DtoDni dato, Pageable pageable) {
        var consulta = consultasService.listarPorPaciente(dato.dni(), pageable);
        return ResponseEntity.ok(consulta.getContent());
    }

    @PostMapping("/find/fecha")
    public ResponseEntity listarPorFecha(@PageableDefault(sort = {"fechaConsulta"})
                                             @RequestBody DtoBuscaPorFecha dato, Pageable pageable) {
        var consulta = consultasService.listarPorFecha(dato.fechaConsulta(), dato.medicoId(), pageable);
        return ResponseEntity.ok(consulta.getContent());
    }
}
