package com.consultorio.oftalmologico.domain.entities.consultorio;

import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Table(name = "horario_consultorio")
@Entity(name = "HorarioConsultorio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class HorarioConsultorio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Consultorio consultorio;
    
    @ManyToOne
    private Usuario medico;
    
    private String diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}
