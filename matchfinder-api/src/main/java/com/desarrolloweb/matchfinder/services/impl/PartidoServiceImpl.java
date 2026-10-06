package com.desarrolloweb.matchfinder.services.impl;

import com.desarrolloweb.matchfinder.dtos.request.PartidoCreateRequest;
import com.desarrolloweb.matchfinder.dtos.response.PartidoResponse;
import com.desarrolloweb.matchfinder.entities.Equipo;
import com.desarrolloweb.matchfinder.entities.Partido;
import com.desarrolloweb.matchfinder.entities.Torneo;
import com.desarrolloweb.matchfinder.entities.enums.EstadoTorneo;
import com.desarrolloweb.matchfinder.exceptions.ConflictoException;
import com.desarrolloweb.matchfinder.exceptions.RecursoNoEncontradoException;
import com.desarrolloweb.matchfinder.exceptions.ReglaNegocioException;
import com.desarrolloweb.matchfinder.repositories.PartidoRepository;
import com.desarrolloweb.matchfinder.repositories.TorneoRepository;
import com.desarrolloweb.matchfinder.services.PartidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartidoServiceImpl implements PartidoService {

    private final PartidoRepository partidoRepository;
    private final TorneoRepository torneoRepository;

    @Override
    @Transactional
    public PartidoResponse registrar(Integer torneoId, PartidoCreateRequest request) {
        Torneo torneo = buscarTorneo(torneoId);

        if (torneo.getEstado() != EstadoTorneo.ACTIVO) {
            throw new ConflictoException("El torneo no está activo");
        }
        if (request.equipoUnoId().equals(request.equipoDosId())) {
            throw new ReglaNegocioException("Los dos equipos del partido deben ser distintos");
        }

        Equipo uno = participante(torneo, request.equipoUnoId());
        Equipo dos = participante(torneo, request.equipoDosId());

        if (!request.ganadorId().equals(uno.getId()) && !request.ganadorId().equals(dos.getId())) {
            throw new ReglaNegocioException("El ganador debe ser uno de los dos equipos del partido");
        }
        Equipo ganador = request.ganadorId().equals(uno.getId()) ? uno : dos;

        return PartidoResponse.desde(partidoRepository.save(Partido.builder()
                .torneo(torneo)
                .equipoUno(uno)
                .equipoDos(dos)
                .ganador(ganador)
                .build()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartidoResponse> listar(Integer torneoId) {
        buscarTorneo(torneoId);
        return partidoRepository.findByTorneoIdOrderByIdAsc(torneoId).stream()
                .map(PartidoResponse::desde)
                .toList();
    }

    @Override
    @Transactional
    public void eliminar(Integer torneoId, Integer partidoId) {
        Partido partido = partidoRepository.findByIdAndTorneoId(partidoId, torneoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Partido con id " + partidoId + " no encontrado en el torneo " + torneoId));
        partidoRepository.delete(partido);
    }

    private Torneo buscarTorneo(Integer id) {
        return torneoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Torneo con id " + id + " no encontrado"));
    }

    private Equipo participante(Torneo torneo, Integer equipoId) {
        return torneo.getEquipos().stream()
                .filter(e -> e.getId().equals(equipoId))
                .findFirst()
                .orElseThrow(() -> new ReglaNegocioException(
                        "El equipo " + equipoId + " no participa en este torneo"));
    }
}
