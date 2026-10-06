package com.desarrolloweb.matchfinder.services;

import com.desarrolloweb.matchfinder.dtos.response.SolicitudAmistadResponse;
import com.desarrolloweb.matchfinder.dtos.response.UsuarioResumenResponse;
import com.desarrolloweb.matchfinder.entities.enums.EstadoSolicitud;

import java.util.List;

public interface AmistadService {

    SolicitudAmistadResponse enviarSolicitud(String email, String nombreDestinatario);

    SolicitudAmistadResponse responderSolicitud(String email, Integer solicitudId, EstadoSolicitud estado);

    List<SolicitudAmistadResponse> solicitudesRecibidas(String email, EstadoSolicitud estado);

    List<SolicitudAmistadResponse> solicitudesEnviadas(String email);

    List<UsuarioResumenResponse> listarAmigos(String email);

    void eliminarAmigo(String email, Integer amigoId);
}
