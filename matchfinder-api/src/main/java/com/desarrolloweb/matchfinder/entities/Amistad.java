package com.desarrolloweb.matchfinder.entities;

import jakarta.persistence.*;
import lombok.*;

/** Una amistad se guarda en ambos sentidos: (A, B) y (B, A). */
@Entity
@Table(name = "amistades",
        uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "amigo_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Amistad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id")
    private MatchFinderUser usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "amigo_id")
    private MatchFinderUser amigo;
}
