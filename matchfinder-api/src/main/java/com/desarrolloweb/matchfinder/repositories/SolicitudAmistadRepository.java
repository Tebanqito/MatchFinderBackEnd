package com.desarrolloweb.matchfinder.repositories;

import com.desarrolloweb.matchfinder.entities.MatchFinderUser;
import com.desarrolloweb.matchfinder.entities.SolicitudAmistad;
import com.desarrolloweb.matchfinder.entities.enums.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudAmistadRepository extends JpaRepository<SolicitudAmistad, Integer> {

    boolean existsByRemitenteAndDestinatarioAndEstado(MatchFinderUser remitente,
                                                      MatchFinderUser destinatario,
                                                      EstadoSolicitud estado);

    Optional<SolicitudAmistad> findFirstByRemitenteAndDestinatarioAndEstado(MatchFinderUser remitente,
                                                                            MatchFinderUser destinatario,
                                                                            EstadoSolicitud estado);

    @Query("select s from SolicitudAmistad s join fetch s.remitente join fetch s.destinatario "
            + "where s.destinatario = :destinatario and s.estado = :estado order by s.id desc")
    List<SolicitudAmistad> recibidas(@Param("destinatario") MatchFinderUser destinatario,
                                     @Param("estado") EstadoSolicitud estado);

    @Query("select s from SolicitudAmistad s join fetch s.remitente join fetch s.destinatario "
            + "where s.remitente = :remitente order by s.id desc")
    List<SolicitudAmistad> enviadas(@Param("remitente") MatchFinderUser remitente);

    void deleteByRemitenteOrDestinatario(MatchFinderUser remitente, MatchFinderUser destinatario);
}
