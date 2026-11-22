package com.desarrolloweb.matchfinder.services;

import com.desarrolloweb.matchfinder.dtos.request.TorneoCreateRequest;
import com.desarrolloweb.matchfinder.dtos.response.LlaveResponse;
import com.desarrolloweb.matchfinder.dtos.response.PaginaResponse;
import com.desarrolloweb.matchfinder.dtos.response.TorneoResponse;
import com.desarrolloweb.matchfinder.dtos.response.TorneoResumenResponse;
import com.desarrolloweb.matchfinder.entities.enums.EstadoTorneo;

public interface TorneoService {

    TorneoResponse crear(TorneoCreateRequest request);

    TorneoResponse actualizarEstado(Integer id, EstadoTorneo estado);

    TorneoResponse obtenerActivo();

    TorneoResponse obtener(Integer id);

    PaginaResponse<TorneoResumenResponse> listar(EstadoTorneo estado, int page, int size);

    LlaveResponse obtenerLlaves(Integer id);
}
