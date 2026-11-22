package com.desarrolloweb.matchfinder.services.impl;

import com.desarrolloweb.matchfinder.dtos.request.TorneoCreateRequest;
import com.desarrolloweb.matchfinder.dtos.response.CruceResponse;
import com.desarrolloweb.matchfinder.dtos.response.EquipoResumenResponse;
import com.desarrolloweb.matchfinder.dtos.response.LlaveResponse;
import com.desarrolloweb.matchfinder.dtos.response.PaginaResponse;
import com.desarrolloweb.matchfinder.dtos.response.RondaResponse;
import com.desarrolloweb.matchfinder.dtos.response.TorneoResponse;
import com.desarrolloweb.matchfinder.dtos.response.TorneoResumenResponse;
import com.desarrolloweb.matchfinder.entities.Equipo;
import com.desarrolloweb.matchfinder.entities.Partido;
import com.desarrolloweb.matchfinder.entities.Torneo;
import com.desarrolloweb.matchfinder.entities.enums.EstadoTorneo;
import com.desarrolloweb.matchfinder.exceptions.ConflictoException;
import com.desarrolloweb.matchfinder.exceptions.RecursoNoEncontradoException;
import com.desarrolloweb.matchfinder.exceptions.ReglaNegocioException;
import com.desarrolloweb.matchfinder.repositories.EquipoRepository;
import com.desarrolloweb.matchfinder.repositories.PartidoRepository;
import com.desarrolloweb.matchfinder.repositories.TorneoRepository;
import com.desarrolloweb.matchfinder.services.TorneoService;
import com.desarrolloweb.matchfinder.services.impl.LlaveCalculador.Cruce;
import com.desarrolloweb.matchfinder.services.impl.LlaveCalculador.Resultado;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TorneoServiceImpl implements TorneoService {

    private final TorneoRepository torneoRepository;
    private final EquipoRepository equipoRepository;
    private final PartidoRepository partidoRepository;

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

        // Selección aleatoria entre los equipos completos del mismo tipo.
        // El orden resultante se guarda: define los cruces de la llave.
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

    @Override
    @Transactional(readOnly = true)
    public PaginaResponse<TorneoResumenResponse> listar(EstadoTorneo estado, int page, int size) {
        // Los más recientes primero
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Torneo> pagina = estado == null
                ? torneoRepository.findAll(pageable)
                : torneoRepository.findByEstado(estado, pageable);
        // Los partidos de todos los torneos de la página se piden juntos, en una sola consulta
        List<Integer> ids = pagina.getContent().stream().map(Torneo::getId).toList();
        Map<Integer, List<Partido>> partidosPorTorneo = ids.isEmpty()
                ? Map.of()
                : partidoRepository.findByTorneoIdInOrderByIdAsc(ids).stream()
                        .collect(Collectors.groupingBy(p -> p.getTorneo().getId()));
        return PaginaResponse.de(pagina.map(t -> resumen(t, partidosPorTorneo.getOrDefault(t.getId(), List.of()))));
    }

    @Override
    @Transactional(readOnly = true)
    public LlaveResponse obtenerLlaves(Integer id) {
        Torneo torneo = buscar(id);
        List<List<Cruce>> rondas = calcularRondas(torneo);

        List<RondaResponse> respuesta = new ArrayList<>();
        for (int k = 0; k < rondas.size(); k++) {
            List<CruceResponse> cruces = new ArrayList<>();
            for (int i = 0; i < rondas.get(k).size(); i++) {
                Cruce c = rondas.get(k).get(i);
                cruces.add(new CruceResponse(i + 1,
                        equipo(torneo, c.equipoUno()),
                        equipo(torneo, c.equipoDos()),
                        c.paseDirecto(),
                        c.partidoId(),
                        equipo(torneo, c.ganador())));
            }
            respuesta.add(new RondaResponse(k + 1, LlaveCalculador.nombreRonda(k, rondas.size()), cruces));
        }

        return new LlaveResponse(torneo.getId(), torneo.getNombre(), torneo.getTipo(), torneo.getEstado(),
                respuesta, equipo(torneo, LlaveCalculador.campeon(rondas)));
    }

    // ---------- auxiliares ----------

    private TorneoResumenResponse resumen(Torneo torneo, List<Partido> partidos) {
        EquipoResumenResponse campeon = equipo(torneo, LlaveCalculador.campeon(calcularRondas(torneo, partidos)));
        return new TorneoResumenResponse(torneo.getId(), torneo.getNombre(), torneo.getTipo(),
                torneo.getCantidadEquipos(), torneo.getEstado(), campeon);
    }

    private List<List<Cruce>> calcularRondas(Torneo torneo) {
        return calcularRondas(torneo, partidoRepository.findByTorneoIdOrderByIdAsc(torneo.getId()));
    }

    private List<List<Cruce>> calcularRondas(Torneo torneo, List<Partido> partidos) {
        List<Integer> ids = torneo.getEquipos().stream().map(Equipo::getId).toList();
        List<Resultado> resultados = partidos.stream()
                .map(p -> new Resultado(p.getId(), p.getEquipoUno().getId(), p.getEquipoDos().getId(),
                        p.getGanador().getId()))
                .toList();
        return LlaveCalculador.calcular(ids, resultados);
    }

    private EquipoResumenResponse equipo(Torneo torneo, Integer equipoId) {
        if (equipoId == null) {
            return null;
        }
        return torneo.getEquipos().stream()
                .filter(e -> e.getId().equals(equipoId))
                .findFirst()
                .map(EquipoResumenResponse::desde)
                .orElse(null);
    }

    private Torneo buscar(Integer id) {
        return torneoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Torneo con id " + id + " no encontrado"));
    }
}
