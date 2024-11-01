package com.consultorio.oftalmologico.domain.entities.consultorio;

import com.consultorio.oftalmologico.domain.entities.clinica.Clinica;
import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
    @Column(columnDefinition = "varchar")
    private String logo;
    private Boolean activo;

    @ManyToOne
    @JoinColumn(name = "clinica_id")
    private Clinica clinica;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

}
