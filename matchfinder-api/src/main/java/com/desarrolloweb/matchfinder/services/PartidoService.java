package com.desarrolloweb.matchfinder.services;

import com.desarrolloweb.matchfinder.dtos.request.PartidoCreateRequest;
import com.desarrolloweb.matchfinder.dtos.response.PartidoResponse;

import java.util.List;

public interface PartidoService {

    PartidoResponse registrar(Integer torneoId, PartidoCreateRequest request);

    List<PartidoResponse> listar(Integer torneoId);

    void eliminar(Integer torneoId, Integer partidoId);
}
