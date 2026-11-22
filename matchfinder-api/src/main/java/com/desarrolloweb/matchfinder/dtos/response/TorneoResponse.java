package com.desarrolloweb.matchfinder.dtos.response;

import com.desarrolloweb.matchfinder.entities.Torneo;
import com.desarrolloweb.matchfinder.entities.enums.EstadoTorneo;
import com.desarrolloweb.matchfinder.entities.enums.TipoEquipo;

import java.util.List;

public record TorneoResponse(
        Integer id,
        String nombre,
        TipoEquipo tipo,
        int cantidadEquipos,
        EstadoTorneo estado,
        List<EquipoResumenResponse> equipos
) {
    /** Los equipos salen en el orden del sorteo. */
    public static TorneoResponse desde(Torneo t) {
        List<EquipoResumenResponse> equipos = t.getEquipos().stream()
                .map(EquipoResumenResponse::desde)
                .toList();
        return new TorneoResponse(t.getId(), t.getNombre(), t.getTipo(),
                t.getCantidadEquipos(), t.getEstado(), equipos);
    }
}
