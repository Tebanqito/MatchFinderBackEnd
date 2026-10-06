package com.desarrolloweb.matchfinder.dtos.response;

import com.desarrolloweb.matchfinder.entities.MatchFinderUser;
import com.desarrolloweb.matchfinder.entities.enums.RolName;

public record UsuarioResponse(Integer id, String nombre, String email, RolName rol, Integer equipoId) {

    public static UsuarioResponse desde(MatchFinderUser u) {
        Integer equipoId = u.getEquipo() == null ? null : u.getEquipo().getId();
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getEmail(), u.getRol(), equipoId);
    }
}
