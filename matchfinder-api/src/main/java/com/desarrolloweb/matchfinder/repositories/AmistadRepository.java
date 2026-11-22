package com.desarrolloweb.matchfinder.repositories;

import com.desarrolloweb.matchfinder.entities.Amistad;
import com.desarrolloweb.matchfinder.entities.MatchFinderUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmistadRepository extends JpaRepository<Amistad, Integer> {

    boolean existsByUsuarioAndAmigo(MatchFinderUser usuario, MatchFinderUser amigo);

    @Query("select a from Amistad a join fetch a.amigo m left join fetch m.equipo "
            + "where a.usuario = :usuario order by m.nombre")
    List<Amistad> listarAmigos(@Param("usuario") MatchFinderUser usuario);

    void deleteByUsuarioAndAmigo(MatchFinderUser usuario, MatchFinderUser amigo);

    void deleteByUsuarioOrAmigo(MatchFinderUser usuario, MatchFinderUser amigo);
}
