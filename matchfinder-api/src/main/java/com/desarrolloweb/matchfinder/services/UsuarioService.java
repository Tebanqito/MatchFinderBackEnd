package com.desarrolloweb.matchfinder.services;

import com.desarrolloweb.matchfinder.dtos.response.PaginaResponse;
import com.desarrolloweb.matchfinder.dtos.response.UsuarioResponse;

public interface UsuarioService {

    PaginaResponse<UsuarioResponse> listar(String nombre, int page, int size);

    void eliminar(Integer id, String emailOwner);
}
