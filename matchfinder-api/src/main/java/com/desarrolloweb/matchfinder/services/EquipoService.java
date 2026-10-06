package com.desarrolloweb.matchfinder.services;

import com.desarrolloweb.matchfinder.dtos.request.EquipoCreateRequest;
import com.desarrolloweb.matchfinder.dtos.request.EquipoUpdateRequest;
import com.desarrolloweb.matchfinder.dtos.response.EquipoDetalleResponse;
import com.desarrolloweb.matchfinder.dtos.response.EquipoResponse;
import com.desarrolloweb.matchfinder.dtos.response.PaginaResponse;
import com.desarrolloweb.matchfinder.entities.MatchFinderUser;
import com.desarrolloweb.matchfinder.entities.enums.TipoEquipo;

public interface EquipoService {

    EquipoDetalleResponse crear(EquipoCreateRequest request, String email);

    PaginaResponse<EquipoResponse> listar(TipoEquipo tipo, String nombre, boolean conCupo, int page, int size);

    EquipoDetalleResponse obtener(Integer id);

    EquipoDetalleResponse editar(Integer id, EquipoUpdateRequest request, String email);

    EquipoDetalleResponse unirse(Integer id, String email);

    EquipoDetalleResponse agregarAmigo(Integer equipoId, Integer amigoId, String email);

    void abandonar(Integer id, String email);

    void eliminar(Integer id);

    /** Uso interno: saca al usuario de su equipo y borra el equipo si queda vacío. */
    void quitarMiembro(MatchFinderUser usuario);
}
