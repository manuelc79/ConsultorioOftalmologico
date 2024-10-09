package com.consultorio.oftalmologico.domain.entities.medico;

import com.consultorio.oftalmologico.domain.entities.paciente.Paciente;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "consultorios")
@Entity(name = "Consultorio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Consultorio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String domicilio;
    private String telefono;
    private String localidad;
    @Lob
    private String logo;
    private Long medicoId;
    private Boolean activo;

}
