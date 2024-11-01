package com.consultorio.oftalmologico.domain.entities.paciente;

import com.consultorio.oftalmologico.domain.entities.clinica.Clinica;
import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Table(name = "pacientes", indexes = {
        @Index(name = "idx_paciente_dni", columnList = "dni", unique = true),
        @Index(name = "idx_paciente_clinica", columnList = "clinica_id")
})
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
    @ManyToOne
    @JoinColumn(name = "clinica_id")
    private Clinica clinica;

    @ManyToMany
    @JoinTable(
        name = "paciente_usuario",
        joinColumns = @JoinColumn(name = "paciente_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<Usuario> usuarios;
}
