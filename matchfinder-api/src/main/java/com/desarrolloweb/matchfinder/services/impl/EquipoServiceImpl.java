package com.desarrolloweb.matchfinder.services.impl;

import com.desarrolloweb.matchfinder.dtos.request.EquipoCreateRequest;
import com.desarrolloweb.matchfinder.dtos.request.EquipoUpdateRequest;
import com.desarrolloweb.matchfinder.dtos.response.EquipoDetalleResponse;
import com.desarrolloweb.matchfinder.dtos.response.EquipoResponse;
import com.desarrolloweb.matchfinder.dtos.response.PaginaResponse;
import com.desarrolloweb.matchfinder.entities.Equipo;
import com.desarrolloweb.matchfinder.entities.MatchFinderUser;
import com.desarrolloweb.matchfinder.entities.enums.EstadoTorneo;
import com.desarrolloweb.matchfinder.entities.enums.TipoEquipo;
import com.desarrolloweb.matchfinder.exceptions.ConflictoException;
import com.desarrolloweb.matchfinder.exceptions.RecursoNoEncontradoException;
import com.desarrolloweb.matchfinder.exceptions.ReglaNegocioException;
import com.desarrolloweb.matchfinder.repositories.AmistadRepository;
import com.desarrolloweb.matchfinder.repositories.EquipoRepository;
import com.desarrolloweb.matchfinder.repositories.MatchFinderUserRepository;
import com.desarrolloweb.matchfinder.repositories.TorneoRepository;
import com.desarrolloweb.matchfinder.security.UsuarioAutenticadoResolver;
import com.desarrolloweb.matchfinder.services.EquipoService;
import jakarta.persistence.criteria.Expression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class EquipoServiceImpl implements EquipoService {

    private final EquipoRepository equipoRepository;
    private final MatchFinderUserRepository userRepository;
    private final TorneoRepository torneoRepository;
    private final AmistadRepository amistadRepository;
    private final UsuarioAutenticadoResolver autenticado;

    @Override
    @Transactional
    public EquipoDetalleResponse crear(EquipoCreateRequest request, String email) {
        MatchFinderUser usuario = autenticado.obtener(email);
        if (usuario.getEquipo() != null) {
            throw new ConflictoException("Ya pertenecés a un equipo: abandonalo antes de crear uno nuevo");
        }

        String nombre = request.nombre().trim();
        if (equipoRepository.existsByNombre(nombre)) {
            throw new ConflictoException("Ya existe un equipo con ese nombre");
        }

        Equipo equipo = equipoRepository.save(Equipo.builder()
                .nombre(nombre)
                .descripcion(request.descripcion().trim())
                .tipo(request.tipo())
                .build());

        // El creador es el primer integrante
        usuario.setEquipo(equipo);
        userRepository.save(usuario);
        equipo.getMiembros().add(usuario);

        return EquipoDetalleResponse.desde(equipo);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaResponse<EquipoResponse> listar(TipoEquipo tipo, String nombre, boolean conCupo,
                                                 int page, int size) {
        Specification<Equipo> spec = (root, query, cb) -> cb.conjunction();

        if (tipo != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("tipo"), tipo));
        }
        if (nombre != null && !nombre.isBlank()) {
            String patron = "%" + nombre.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("nombre")), patron));
        }
        if (conCupo) {
            spec = spec.and((root, query, cb) -> {
                Expression<Integer> cupo = cb.<Integer>selectCase()
                        .when(cb.equal(root.get("tipo"), TipoEquipo.FUTBOL_5), TipoEquipo.FUTBOL_5.getCupo())
                        .otherwise(TipoEquipo.FUTBOL_11.getCupo());
                return cb.lessThan(cb.size(root.<Collection<MatchFinderUser>>get("miembros")), cupo);
            });
        }

        return PaginaResponse.de(
                equipoRepository.findAll(spec, PageRequest.of(page, size, Sort.by("id")))
                        .map(EquipoResponse::desde));
    }

    @Override
    @Transactional(readOnly = true)
    public EquipoDetalleResponse obtener(Integer id) {
        return EquipoDetalleResponse.desde(buscar(id));
    }

    @Override
    @Transactional
    public EquipoDetalleResponse editar(Integer id, EquipoUpdateRequest request, String email) {
        Equipo equipo = buscar(id);
        MatchFinderUser usuario = autenticado.obtener(email);
        exigirMiembro(usuario, id);

        if (request.nombre() == null && request.descripcion() == null) {
            throw new ReglaNegocioException("Tenés que enviar al menos el nombre o la descripción");
        }

        if (request.nombre() != null) {
            String nombre = request.nombre().trim();
            if (equipoRepository.existsByNombreAndIdNot(nombre, id)) {
                throw new ConflictoException("Ya existe un equipo con ese nombre");
            }
            equipo.setNombre(nombre);
        }
        if (request.descripcion() != null) {
            equipo.setDescripcion(request.descripcion().trim());
        }

        return EquipoDetalleResponse.desde(equipo);
    }

    @Override
    @Transactional
    public EquipoDetalleResponse unirse(Integer id, String email) {
        MatchFinderUser usuario = autenticado.obtener(email);
        Equipo equipo = bloquear(id);

        if (usuario.getEquipo() != null) {
            throw new ConflictoException("Ya pertenecés a un equipo");
        }

        agregarMiembro(equipo, usuario);
        return EquipoDetalleResponse.desde(equipo);
    }

    @Override
    @Transactional
    public EquipoDetalleResponse agregarAmigo(Integer equipoId, Integer amigoId, String email) {
        MatchFinderUser actor = autenticado.obtener(email);
        Equipo equipo = bloquear(equipoId);
        exigirMiembro(actor, equipoId);

        MatchFinderUser amigo = userRepository.findById(amigoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario con id " + amigoId + " no encontrado"));

        if (!amistadRepository.existsByUsuarioAndAmigo(actor, amigo)) {
            throw new ReglaNegocioException("Solo podés agregar al equipo a usuarios que sean tus amigos");
        }
        if (amigo.getEquipo() != null) {
            throw new ConflictoException("El usuario ya pertenece a un equipo");
        }

        agregarMiembro(equipo, amigo);
        return EquipoDetalleResponse.desde(equipo);
    }

    @Override
    @Transactional
    public void abandonar(Integer id, String email) {
        MatchFinderUser usuario = autenticado.obtener(email);
        buscar(id);
        exigirMiembro(usuario, id);

        if (torneoRepository.existeParticipacion(id, EstadoTorneo.ACTIVO)) {
            throw new ConflictoException("No podés abandonar un equipo que participa del torneo activo");
        }

        quitarMiembro(usuario);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        buscar(id);

        if (torneoRepository.existeParticipacion(id, EstadoTorneo.ACTIVO)) {
            throw new ConflictoException("No se puede eliminar un equipo que participa del torneo activo");
        }
        if (torneoRepository.existeParticipacionHistorica(id)) {
            throw new ConflictoException("No se puede eliminar un equipo que participó en torneos anteriores");
        }

        userRepository.liberarMiembros(id);
        equipoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void quitarMiembro(MatchFinderUser usuario) {
        Equipo equipo = usuario.getEquipo();
        if (equipo == null) {
            return;
        }
        Integer equipoId = equipo.getId();

        usuario.setEquipo(null);
        userRepository.saveAndFlush(usuario);

        // Si el equipo queda vacío se borra, salvo que tenga historial de torneos
        if (userRepository.contarMiembros(equipoId) == 0
                && !torneoRepository.existeParticipacionHistorica(equipoId)) {
            equipoRepository.deleteById(equipoId);
        }
    }

    // ---------- auxiliares ----------

    private Equipo buscar(Integer id) {
        return equipoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Equipo con id " + id + " no encontrado"));
    }

    private Equipo bloquear(Integer id) {
        return equipoRepository.buscarParaActualizar(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Equipo con id " + id + " no encontrado"));
    }

    private void exigirMiembro(MatchFinderUser usuario, Integer equipoId) {
        if (usuario.getEquipo() == null || !usuario.getEquipo().getId().equals(equipoId)) {
            throw new AccessDeniedException("No pertenecés a este equipo");
        }
    }

    private void agregarMiembro(Equipo equipo, MatchFinderUser usuario) {
        if (equipo.getMiembros().size() >= equipo.getTipo().getCupo()) {
            throw new ConflictoException("El equipo no tiene cupo disponible");
        }
        usuario.setEquipo(equipo);
        userRepository.save(usuario);
        equipo.getMiembros().add(usuario);
    }
}
