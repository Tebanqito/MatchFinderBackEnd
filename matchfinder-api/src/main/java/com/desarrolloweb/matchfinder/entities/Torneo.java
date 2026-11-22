package com.desarrolloweb.matchfinder.entities;

import com.desarrolloweb.matchfinder.entities.enums.EstadoTorneo;
import com.desarrolloweb.matchfinder.entities.enums.TipoEquipo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "torneos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Torneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoEquipo tipo;

    @Column(nullable = false)
    private int cantidadEquipos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTorneo estado;

    /**
     * Equipos sorteados, en el orden del sorteo (columna "orden" de torneo_equipos).
     * Ese orden define los cruces de la llave.
     */
    @ManyToMany
    @OrderColumn(name = "orden")
    @BatchSize(size = 50)
    @JoinTable(name = "torneo_equipos",
            joinColumns = @JoinColumn(name = "torneo_id"),
            inverseJoinColumns = @JoinColumn(name = "equipo_id"))
    @Builder.Default
    private List<Equipo> equipos = new ArrayList<>();
}
