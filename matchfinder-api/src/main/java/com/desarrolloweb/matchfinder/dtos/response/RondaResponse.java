package com.desarrolloweb.matchfinder.dtos.response;

import java.util.List;

public record RondaResponse(
        int numero,
        String nombre,
        List<CruceResponse> cruces
) { }
