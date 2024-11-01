package com.consultorio.oftalmologico.domain.entities.historiaclinica;

import com.consultorio.oftalmologico.domain.entities.paciente.Paciente;
import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "consulta_medica")
@Entity(name = "ConsultaMedica")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ConsultaMedica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Paciente paciente;
    
    @ManyToOne
    private Usuario medico;
    
    private LocalDateTime fechaConsulta;
}
