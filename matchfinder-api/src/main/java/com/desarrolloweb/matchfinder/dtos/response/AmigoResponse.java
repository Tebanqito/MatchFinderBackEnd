package com.desarrolloweb.matchfinder.dtos.response;

import com.desarrolloweb.matchfinder.entities.MatchFinderUser;

/** Un amigo de la lista. equipoId es null si el amigo todavía no pertenece a ningún equipo. */
public record AmigoResponse(Integer id, String nombre, Integer equipoId) {

    public static AmigoResponse desde(MatchFinderUser u) {
        return new AmigoResponse(u.getId(), u.getNombre(), u.getEquipo() == null ? null : u.getEquipo().getId());
    }
}
