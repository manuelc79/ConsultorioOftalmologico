package com.consultorio.oftalmologico.domain.entities.clinica;

import com.consultorio.oftalmologico.domain.entities.consultorio.Consultorio;
import com.consultorio.oftalmologico.domain.entities.paciente.Paciente;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Table(name = "clinicas")
@Entity(name = "Clinica")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Clinica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String domicilio;
    @Column(unique = true)
    private String identificacionFiscal;

    @OneToMany(mappedBy = "clinica", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Consultorio> consultorios;

    @OneToMany(mappedBy = "clinica", cascade = CascadeType.ALL)
    private List<Paciente> pacientes;

    private Boolean activo;
}
