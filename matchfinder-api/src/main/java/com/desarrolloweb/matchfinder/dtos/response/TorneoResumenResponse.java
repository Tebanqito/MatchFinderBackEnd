package com.desarrolloweb.matchfinder.dtos.response;

import com.desarrolloweb.matchfinder.entities.enums.EstadoTorneo;
import com.desarrolloweb.matchfinder.entities.enums.TipoEquipo;

/** Fila del listado de torneos. El campeón es null mientras la final no tenga resultado. */
public record TorneoResumenResponse(
        Integer id,
        String nombre,
        TipoEquipo tipo,
        int cantidadEquipos,
        EstadoTorneo estado,
        EquipoResumenResponse campeon
) { }
