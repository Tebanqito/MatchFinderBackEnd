package com.desarrolloweb.matchfinder.dtos.response;

import com.desarrolloweb.matchfinder.entities.enums.EstadoTorneo;
import com.desarrolloweb.matchfinder.entities.enums.TipoEquipo;

import java.util.List;

public record LlaveResponse(
        Integer torneoId,
        String nombre,
        TipoEquipo tipo,
        EstadoTorneo estado,
        List<RondaResponse> rondas,
        EquipoResumenResponse campeon
) { }
