package com.desarrolloweb.matchfinder.dtos.response;

/**
 * Un cruce de la llave. Los equipos son null mientras no se sepa quién llega.
 * partidoId y ganador son null mientras el cruce no tenga resultado.
 */
public record CruceResponse(
        Integer posicion,
        EquipoResumenResponse equipoUno,
        EquipoResumenResponse equipoDos,
        boolean paseDirecto,
        Integer partidoId,
        EquipoResumenResponse ganador
) { }
