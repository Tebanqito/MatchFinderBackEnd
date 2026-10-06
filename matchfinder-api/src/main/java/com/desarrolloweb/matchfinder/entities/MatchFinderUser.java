package com.desarrolloweb.matchfinder.entities;

import com.desarrolloweb.matchfinder.entities.enums.RolName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchFinderUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password; // hash BCrypt

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolName rol;

    // Una sola columna: un usuario solo puede pertenecer a un equipo a la vez
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_id")
    private Equipo equipo;
}
