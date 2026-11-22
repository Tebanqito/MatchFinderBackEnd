package com.desarrolloweb.matchfinder.repositories;

import com.desarrolloweb.matchfinder.entities.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Integer> {

    List<Partido> findByTorneoIdOrderByIdAsc(Integer torneoId);

    List<Partido> findByTorneoIdInOrderByIdAsc(Collection<Integer> torneoIds);

    Optional<Partido> findByIdAndTorneoId(Integer id, Integer torneoId);
}
