package com.desarrolloweb.matchfinder.repositories;

import com.desarrolloweb.matchfinder.entities.MatchFinderUser;
import com.desarrolloweb.matchfinder.entities.SolicitudAmistad;
import com.desarrolloweb.matchfinder.entities.enums.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
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

    List<SolicitudAmistad> findByDestinatarioAndEstadoOrderByIdDesc(MatchFinderUser destinatario,
                                                                    EstadoSolicitud estado);

    List<SolicitudAmistad> findByRemitenteOrderByIdDesc(MatchFinderUser remitente);

    void deleteByRemitenteOrDestinatario(MatchFinderUser remitente, MatchFinderUser destinatario);
}
