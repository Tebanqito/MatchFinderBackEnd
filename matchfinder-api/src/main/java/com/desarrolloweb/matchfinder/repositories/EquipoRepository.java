package com.desarrolloweb.matchfinder.repositories;

import com.desarrolloweb.matchfinder.entities.Equipo;
import com.desarrolloweb.matchfinder.entities.enums.TipoEquipo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Integer>, JpaSpecificationExecutor<Equipo> {

    boolean existsByNombre(String nombre);

    boolean existsByNombreAndIdNot(String nombre, Integer id);

    /** Equipos del tipo indicado con el cupo completo. */
    @Query("select e from Equipo e where e.tipo = :tipo and size(e.miembros) = :cupo")
    List<Equipo> findCompletos(@Param("tipo") TipoEquipo tipo, @Param("cupo") int cupo);

    /** Bloquea la fila del equipo para que dos altas simultáneas no superen el cupo. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Equipo e where e.id = :id")
    Optional<Equipo> buscarParaActualizar(@Param("id") Integer id);
}
