package com.desarrolloweb.matchfinder.repositories;

import com.desarrolloweb.matchfinder.entities.MatchFinderUser;
import com.desarrolloweb.matchfinder.entities.enums.RolName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MatchFinderUserRepository extends JpaRepository<MatchFinderUser, Integer> {

    Optional<MatchFinderUser> findByEmail(String email);

    Optional<MatchFinderUser> findByNombre(String nombre);

    boolean existsByEmail(String email);

    boolean existsByNombre(String nombre);

    boolean existsByRol(RolName rol);

    Page<MatchFinderUser> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    @Query("select count(u) from MatchFinderUser u where u.equipo.id = :equipoId")
    long contarMiembros(@Param("equipoId") Integer equipoId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update MatchFinderUser u set u.equipo = null where u.equipo.id = :equipoId")
    int liberarMiembros(@Param("equipoId") Integer equipoId);
}
