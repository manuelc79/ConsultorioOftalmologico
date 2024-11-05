package com.consultorio.oftalmologico.domain.entities.historiaclinica;

import java.time.LocalDate;

import com.consultorio.oftalmologico.domain.entities.clinica.Clinica;
import com.consultorio.oftalmologico.domain.entities.paciente.Paciente;
import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "historia_clinica", indexes = {
        @Index(name = "idx_hc_paciente_fecha", columnList = "paciente_dni, fecha_consulta"),
        @Index(name = "idx_hc_usuario_fecha", columnList = "usuario_id, fecha_consulta")
})
@Entity(name = "HistoriaClinica")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class HistoriaClinica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate fechaConsulta;
    private String agudezaVisualOISC; // Agudeza Visual Ojo Izquierdo S/C
    private String agudezaVisualODSC; // Agudeza Visual Ojo Derecho S/C
    private String agudezaVisualOICC; // Agudeza Visual Ojo Izquierdo C/C
    private String agudezaVisualODCC; // Agudeza Visual Ojo Derecho C/C
    private String lentesParaLejosOI; // Lentes Para Lejos Ojo Izquierdo
    private String lentesParaLejosOD; // Lentes Para Lejos Ojo Derecho
    private String lentesParaCercaAO; // Lentes Para Cerca Ambos Ojos
    private String observaciones;
    private Boolean activo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinica_id")
    private Clinica clinica;
}
