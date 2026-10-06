package com.desarrolloweb.matchfinder.dtos.response;

import com.desarrolloweb.matchfinder.entities.Partido;

public record PartidoResponse(
        Integer id,
        Integer torneoId,
        EquipoResumenResponse equipoUno,
        EquipoResumenResponse equipoDos,
        EquipoResumenResponse ganador
) {
    public static PartidoResponse desde(Partido p) {
        return new PartidoResponse(p.getId(), p.getTorneo().getId(),
                EquipoResumenResponse.desde(p.getEquipoUno()),
                EquipoResumenResponse.desde(p.getEquipoDos()),
                EquipoResumenResponse.desde(p.getGanador()));
    }
}
