package com.consultorio.oftalmologico.domain.entities.medico;

import com.consultorio.oftalmologico.domain.entities.paciente.Paciente;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "detalles_medicos")
@Entity(name = "DetallesMedico")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class DetallesMedico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;
    private String especialidad;
    private Long numeroMatricula;
    private Long telefono;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id")
    @JsonManagedReference
    private Medico medico;
}
