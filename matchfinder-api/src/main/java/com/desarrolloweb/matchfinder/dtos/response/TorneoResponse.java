package com.desarrolloweb.matchfinder.dtos.response;

import com.desarrolloweb.matchfinder.entities.Equipo;
import com.desarrolloweb.matchfinder.entities.Torneo;
import com.desarrolloweb.matchfinder.entities.enums.EstadoTorneo;
import com.desarrolloweb.matchfinder.entities.enums.TipoEquipo;

import java.util.Comparator;
import java.util.List;

public record TorneoResponse(
        Integer id,
        String nombre,
        TipoEquipo tipo,
        int cantidadEquipos,
        EstadoTorneo estado,
        List<EquipoResumenResponse> equipos
) {
    public static TorneoResponse desde(Torneo t) {
        List<EquipoResumenResponse> equipos = t.getEquipos().stream()
                .sorted(Comparator.comparing(Equipo::getId))
                .map(EquipoResumenResponse::desde)
                .toList();
        return new TorneoResponse(t.getId(), t.getNombre(), t.getTipo(),
                t.getCantidadEquipos(), t.getEstado(), equipos);
    }
}
