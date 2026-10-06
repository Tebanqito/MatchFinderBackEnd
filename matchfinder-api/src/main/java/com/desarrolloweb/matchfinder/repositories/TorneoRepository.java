package com.desarrolloweb.matchfinder.repositories;

import com.desarrolloweb.matchfinder.entities.Torneo;
import com.desarrolloweb.matchfinder.entities.enums.EstadoTorneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TorneoRepository extends JpaRepository<Torneo, Integer> {

    boolean existsByEstado(EstadoTorneo estado);

    Optional<Torneo> findFirstByEstado(EstadoTorneo estado);

    /** ¿El equipo participa en algún torneo con ese estado? */
    @Query("select count(t) > 0 from Torneo t join t.equipos e where e.id = :equipoId and t.estado = :estado")
    boolean existeParticipacion(@Param("equipoId") Integer equipoId, @Param("estado") EstadoTorneo estado);

    /** ¿El equipo participó alguna vez en un torneo (activo o finalizado)? */
    @Query("select count(t) > 0 from Torneo t join t.equipos e where e.id = :equipoId")
    boolean existeParticipacionHistorica(@Param("equipoId") Integer equipoId);
}
