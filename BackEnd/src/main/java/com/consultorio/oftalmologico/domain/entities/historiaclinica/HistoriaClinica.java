package com.consultorio.oftalmologico.domain.entities.historiaclinica;

import com.consultorio.oftalmologico.domain.entities.paciente.Paciente;
import com.consultorio.oftalmologico.domain.entities.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private Long pacienteDni;
    //private Long usuarioId;
    private Long clinicaId;
    private Boolean activo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
