package com.desarrolloweb.matchfinder.dtos.response;

import com.desarrolloweb.matchfinder.entities.Equipo;
import com.desarrolloweb.matchfinder.entities.MatchFinderUser;
import com.desarrolloweb.matchfinder.entities.enums.TipoEquipo;

import java.util.Comparator;
import java.util.List;

public record EquipoDetalleResponse(
        Integer id,
        String nombre,
        String descripcion,
        TipoEquipo tipo,
        int cupo,
        int cantidadMiembros,
        List<UsuarioResumenResponse> miembros
) {
    public static EquipoDetalleResponse desde(Equipo e) {
        List<UsuarioResumenResponse> miembros = e.getMiembros().stream()
                .sorted(Comparator.comparing(MatchFinderUser::getId))
                .map(UsuarioResumenResponse::desde)
                .toList();
        return new EquipoDetalleResponse(e.getId(), e.getNombre(), e.getDescripcion(), e.getTipo(),
                e.getTipo().getCupo(), miembros.size(), miembros);
    }
}
