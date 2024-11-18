package com.consultorio.oftalmologico.domain.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "registro_actividad")
@Entity(name = "RegistroActividad")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class RegistroActividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId; // ID del usuario que realizó la acción
    private String tipoOperacion; // Tipo de operación (ej. "CREAR", "MODIFICAR", "ELIMINAR")
    private String descripcion; // Descripción de la acción realizada
    private LocalDateTime fechaOperacion; // Fecha y hora de la operación


}
