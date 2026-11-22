package com.desarrolloweb.matchfinder.entities;

import com.desarrolloweb.matchfinder.entities.enums.TipoEquipo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipos")
@BatchSize(size = 100)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoEquipo tipo;

    @OneToMany(mappedBy = "equipo")
    @BatchSize(size = 100)
    @Builder.Default
    private List<MatchFinderUser> miembros = new ArrayList<>();
}
