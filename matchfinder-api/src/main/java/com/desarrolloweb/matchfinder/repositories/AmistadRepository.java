package com.desarrolloweb.matchfinder.repositories;

import com.desarrolloweb.matchfinder.entities.Amistad;
import com.desarrolloweb.matchfinder.entities.MatchFinderUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmistadRepository extends JpaRepository<Amistad, Integer> {

    boolean existsByUsuarioAndAmigo(MatchFinderUser usuario, MatchFinderUser amigo);

    List<Amistad> findByUsuarioOrderByAmigoNombreAsc(MatchFinderUser usuario);

    void deleteByUsuarioAndAmigo(MatchFinderUser usuario, MatchFinderUser amigo);

    void deleteByUsuarioOrAmigo(MatchFinderUser usuario, MatchFinderUser amigo);
}
