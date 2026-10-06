package com.desarrolloweb.matchfinder.dtos.response;

import com.desarrolloweb.matchfinder.entities.SolicitudAmistad;
import com.desarrolloweb.matchfinder.entities.enums.EstadoSolicitud;

public record SolicitudAmistadResponse(
        Integer id,
        UsuarioResumenResponse remitente,
        UsuarioResumenResponse destinatario,
        EstadoSolicitud estado
) {
    public static SolicitudAmistadResponse desde(SolicitudAmistad s) {
        return new SolicitudAmistadResponse(s.getId(),
                UsuarioResumenResponse.desde(s.getRemitente()),
                UsuarioResumenResponse.desde(s.getDestinatario()),
                s.getEstado());
    }
}
