package com.consultorio.oftalmologico.domain.entities.clinica;

import com.consultorio.oftalmologico.domain.entities.medico.Consultorio;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "clinicas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Clinica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    private String direccion;
    private String telefono;
    
    @OneToMany(mappedBy = "clinica", cascade = CascadeType.ALL)
    private List<Consultorio> consultorios;
}
