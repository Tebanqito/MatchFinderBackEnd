package com.desarrolloweb.matchfinder.services.impl;

import com.desarrolloweb.matchfinder.dtos.request.TorneoCreateRequest;
import com.desarrolloweb.matchfinder.dtos.response.TorneoResponse;
import com.desarrolloweb.matchfinder.entities.Equipo;
import com.desarrolloweb.matchfinder.entities.Torneo;
import com.desarrolloweb.matchfinder.entities.enums.EstadoTorneo;
import com.desarrolloweb.matchfinder.exceptions.ConflictoException;
import com.desarrolloweb.matchfinder.exceptions.RecursoNoEncontradoException;
import com.desarrolloweb.matchfinder.exceptions.ReglaNegocioException;
import com.desarrolloweb.matchfinder.repositories.EquipoRepository;
import com.desarrolloweb.matchfinder.repositories.TorneoRepository;
import com.desarrolloweb.matchfinder.services.TorneoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TorneoServiceImpl implements TorneoService {

    private final TorneoRepository torneoRepository;
    private final EquipoRepository equipoRepository;

    @Override
    @Transactional
    public TorneoResponse crear(TorneoCreateRequest request) {
        if (torneoRepository.existsByEstado(EstadoTorneo.ACTIVO)) {
            throw new ConflictoException("Ya existe un torneo activo: finalizalo antes de crear otro");
        }

        int necesarios = request.cantidadEquipos();
        List<Equipo> elegibles = new ArrayList<>(
                equipoRepository.findCompletos(request.tipo(), request.tipo().getCupo()));

        if (elegibles.size() < necesarios) {
            throw new ConflictoException(String.format(
                    "No hay suficientes equipos elegibles: el torneo necesita %d equipos completos de tipo %s y hay %d",
                    necesarios, request.tipo(), elegibles.size()));
        }

        // Selección aleatoria entre los equipos completos del mismo tipo
        Collections.shuffle(elegibles);

        Torneo torneo = Torneo.builder()
                .nombre(request.nombre().trim())
                .tipo(request.tipo())
                .cantidadEquipos(necesarios)
                .estado(EstadoTorneo.ACTIVO)
                .build();
        torneo.getEquipos().addAll(elegibles.subList(0, necesarios));

        return TorneoResponse.desde(torneoRepository.save(torneo));
    }

    @Override
    @Transactional
    public TorneoResponse actualizarEstado(Integer id, EstadoTorneo estado) {
        if (estado != EstadoTorneo.FINALIZADO) {
            throw new ReglaNegocioException("Solo se puede cambiar el estado a FINALIZADO");
        }

        Torneo torneo = buscar(id);
        if (torneo.getEstado() == EstadoTorneo.FINALIZADO) {
            throw new ConflictoException("El torneo ya está finalizado");
        }

        torneo.setEstado(EstadoTorneo.FINALIZADO);
        return TorneoResponse.desde(torneo);
    }

    @Override
    @Transactional(readOnly = true)
    public TorneoResponse obtenerActivo() {
        return TorneoResponse.desde(torneoRepository.findFirstByEstado(EstadoTorneo.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException("No hay un torneo activo")));
    }

    @Override
    @Transactional(readOnly = true)
    public TorneoResponse obtener(Integer id) {
        return TorneoResponse.desde(buscar(id));
    }

    private Torneo buscar(Integer id) {
        return torneoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Torneo con id " + id + " no encontrado"));
    }
}
