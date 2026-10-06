package com.desarrolloweb.matchfinder.dtos.response;

import com.desarrolloweb.matchfinder.entities.MatchFinderUser;

public record UsuarioResumenResponse(Integer id, String nombre) {

    public static UsuarioResumenResponse desde(MatchFinderUser u) {
        return new UsuarioResumenResponse(u.getId(), u.getNombre());
    }
}
