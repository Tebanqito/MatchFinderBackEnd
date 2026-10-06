package com.desarrolloweb.matchfinder.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "partidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "torneo_id")
    private Torneo torneo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipo_uno_id")
    private Equipo equipoUno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipo_dos_id")
    private Equipo equipoDos;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ganador_id")
    private Equipo ganador;
}
