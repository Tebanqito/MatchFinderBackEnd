package com.desarrolloweb.matchfinder.dtos.response;

import com.desarrolloweb.matchfinder.entities.Equipo;
import com.desarrolloweb.matchfinder.entities.enums.TipoEquipo;

public record EquipoResponse(
        Integer id,
        String nombre,
        String descripcion,
        TipoEquipo tipo,
        int cupo,
        int cantidadMiembros
) {
    public static EquipoResponse desde(Equipo e) {
        return new EquipoResponse(e.getId(), e.getNombre(), e.getDescripcion(), e.getTipo(),
                e.getTipo().getCupo(), e.getMiembros().size());
    }
}
