package com.consultorio.oftalmologico.domain.entities.paciente;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Table(name = "pacientes")
@Entity(name = "Paciente")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String apellido;
    private String nombre;
    private String telefono;
    @Column(unique = true)
    private Long dni;
    private String ObraSocial;
    private String numeroObraSocial;
    private Boolean activo;
}
