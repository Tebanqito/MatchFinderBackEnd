package com.desarrolloweb.matchfinder.services.impl;

import com.desarrolloweb.matchfinder.dtos.response.SolicitudAmistadResponse;
import com.desarrolloweb.matchfinder.dtos.response.UsuarioResumenResponse;
import com.desarrolloweb.matchfinder.entities.Amistad;
import com.desarrolloweb.matchfinder.entities.MatchFinderUser;
import com.desarrolloweb.matchfinder.entities.SolicitudAmistad;
import com.desarrolloweb.matchfinder.entities.enums.EstadoSolicitud;
import com.desarrolloweb.matchfinder.entities.enums.RolName;
import com.desarrolloweb.matchfinder.exceptions.ConflictoException;
import com.desarrolloweb.matchfinder.exceptions.RecursoNoEncontradoException;
import com.desarrolloweb.matchfinder.exceptions.ReglaNegocioException;
import com.desarrolloweb.matchfinder.repositories.AmistadRepository;
import com.desarrolloweb.matchfinder.repositories.MatchFinderUserRepository;
import com.desarrolloweb.matchfinder.repositories.SolicitudAmistadRepository;
import com.desarrolloweb.matchfinder.security.UsuarioAutenticadoResolver;
import com.desarrolloweb.matchfinder.services.AmistadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AmistadServiceImpl implements AmistadService {

    private final SolicitudAmistadRepository solicitudRepository;
    private final AmistadRepository amistadRepository;
    private final MatchFinderUserRepository userRepository;
    private final UsuarioAutenticadoResolver autenticado;

    @Override
    @Transactional
    public SolicitudAmistadResponse enviarSolicitud(String email, String nombreDestinatario) {
        MatchFinderUser remitente = autenticado.obtener(email);
        MatchFinderUser destinatario = userRepository.findByNombre(nombreDestinatario.trim())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un usuario con ese nombre"));

        if (remitente.getId().equals(destinatario.getId())) {
            throw new ReglaNegocioException("No podés enviarte una solicitud a vos mismo");
        }
        if (destinatario.getRol() == RolName.OWNER) {
            throw new ReglaNegocioException("Ese usuario no puede ser agregado como amigo");
        }
        if (amistadRepository.existsByUsuarioAndAmigo(remitente, destinatario)) {
            throw new ConflictoException("Ya son amigos");
        }
        if (solicitudRepository.existsByRemitenteAndDestinatarioAndEstado(
                remitente, destinatario, EstadoSolicitud.PENDIENTE)) {
            throw new ConflictoException("Ya enviaste una solicitud pendiente a este usuario");
        }

        // Si el otro ya le había mandado una solicitud, se acepta sola
        Optional<SolicitudAmistad> inversa = solicitudRepository.findFirstByRemitenteAndDestinatarioAndEstado(
                destinatario, remitente, EstadoSolicitud.PENDIENTE);
        if (inversa.isPresent()) {
            SolicitudAmistad existente = inversa.get();
            existente.setEstado(EstadoSolicitud.ACEPTADA);
            crearAmistad(existente.getRemitente(), existente.getDestinatario());
            return SolicitudAmistadResponse.desde(existente);
        }

        return SolicitudAmistadResponse.desde(solicitudRepository.save(SolicitudAmistad.builder()
                .remitente(remitente)
                .destinatario(destinatario)
                .estado(EstadoSolicitud.PENDIENTE)
                .build()));
    }

    @Override
    @Transactional
    public SolicitudAmistadResponse responderSolicitud(String email, Integer solicitudId, EstadoSolicitud estado) {
        if (estado == EstadoSolicitud.PENDIENTE) {
            throw new ReglaNegocioException("El estado debe ser ACEPTADA o RECHAZADA");
        }

        MatchFinderUser usuario = autenticado.obtener(email);
        SolicitudAmistad solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Solicitud con id " + solicitudId + " no encontrada"));

        if (!solicitud.getDestinatario().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("Solo el destinatario puede responder la solicitud");
        }
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new ConflictoException("La solicitud ya fue respondida");
        }

        solicitud.setEstado(estado);
        if (estado == EstadoSolicitud.ACEPTADA) {
            crearAmistad(solicitud.getRemitente(), solicitud.getDestinatario());
        }
        return SolicitudAmistadResponse.desde(solicitud);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudAmistadResponse> solicitudesRecibidas(String email, EstadoSolicitud estado) {
        MatchFinderUser usuario = autenticado.obtener(email);
        return solicitudRepository.findByDestinatarioAndEstadoOrderByIdDesc(usuario, estado).stream()
                .map(SolicitudAmistadResponse::desde)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudAmistadResponse> solicitudesEnviadas(String email) {
        MatchFinderUser usuario = autenticado.obtener(email);
        return solicitudRepository.findByRemitenteOrderByIdDesc(usuario).stream()
                .map(SolicitudAmistadResponse::desde)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResumenResponse> listarAmigos(String email) {
        MatchFinderUser usuario = autenticado.obtener(email);
        return amistadRepository.findByUsuarioOrderByAmigoNombreAsc(usuario).stream()
                .map(a -> UsuarioResumenResponse.desde(a.getAmigo()))
                .toList();
    }

    @Override
    @Transactional
    public void eliminarAmigo(String email, Integer amigoId) {
        MatchFinderUser usuario = autenticado.obtener(email);
        MatchFinderUser amigo = userRepository.findById(amigoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario con id " + amigoId + " no encontrado"));

        if (!amistadRepository.existsByUsuarioAndAmigo(usuario, amigo)) {
            throw new RecursoNoEncontradoException("Ese usuario no está en tu lista de amigos");
        }

        // La amistad se borra de ambos lados
        amistadRepository.deleteByUsuarioAndAmigo(usuario, amigo);
        amistadRepository.deleteByUsuarioAndAmigo(amigo, usuario);
    }

    private void crearAmistad(MatchFinderUser a, MatchFinderUser b) {
        if (!amistadRepository.existsByUsuarioAndAmigo(a, b)) {
            amistadRepository.save(Amistad.builder().usuario(a).amigo(b).build());
        }
        if (!amistadRepository.existsByUsuarioAndAmigo(b, a)) {
            amistadRepository.save(Amistad.builder().usuario(b).amigo(a).build());
        }
    }
}
