package com.desarrolloweb.matchfinder.services.impl;

import com.desarrolloweb.matchfinder.dtos.response.PaginaResponse;
import com.desarrolloweb.matchfinder.dtos.response.UsuarioResponse;
import com.desarrolloweb.matchfinder.entities.MatchFinderUser;
import com.desarrolloweb.matchfinder.exceptions.ConflictoException;
import com.desarrolloweb.matchfinder.exceptions.RecursoNoEncontradoException;
import com.desarrolloweb.matchfinder.repositories.AmistadRepository;
import com.desarrolloweb.matchfinder.repositories.MatchFinderUserRepository;
import com.desarrolloweb.matchfinder.repositories.SolicitudAmistadRepository;
import com.desarrolloweb.matchfinder.security.UsuarioAutenticadoResolver;
import com.desarrolloweb.matchfinder.services.EquipoService;
import com.desarrolloweb.matchfinder.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final MatchFinderUserRepository userRepository;
    private final AmistadRepository amistadRepository;
    private final SolicitudAmistadRepository solicitudRepository;
    private final EquipoService equipoService;
    private final UsuarioAutenticadoResolver autenticado;

    @Override
    @Transactional(readOnly = true)
    public PaginaResponse<UsuarioResponse> listar(String nombre, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        Page<MatchFinderUser> resultado = (nombre == null || nombre.isBlank())
                ? userRepository.findAll(pageable)
                : userRepository.findByNombreContainingIgnoreCase(nombre.trim(), pageable);
        return PaginaResponse.de(resultado.map(UsuarioResponse::desde));
    }

    @Override
    @Transactional
    public void eliminar(Integer id, String emailOwner) {
        MatchFinderUser owner = autenticado.obtener(emailOwner);
        MatchFinderUser objetivo = userRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario con id " + id + " no encontrado"));

        if (objetivo.getId().equals(owner.getId())) {
            throw new ConflictoException("El owner no puede eliminarse a sí mismo");
        }

        // Se lo saca del equipo (liberando el cupo) y se limpian sus amistades y solicitudes
        equipoService.quitarMiembro(objetivo);
        amistadRepository.deleteByUsuarioOrAmigo(objetivo, objetivo);
        solicitudRepository.deleteByRemitenteOrDestinatario(objetivo, objetivo);
        userRepository.delete(objetivo);
    }
}
