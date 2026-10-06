package com.desarrolloweb.matchfinder.dtos.response;

import com.desarrolloweb.matchfinder.entities.Equipo;

public record EquipoResumenResponse(Integer id, String nombre) {

    public static EquipoResumenResponse desde(Equipo e) {
        return new EquipoResumenResponse(e.getId(), e.getNombre());
    }
}
