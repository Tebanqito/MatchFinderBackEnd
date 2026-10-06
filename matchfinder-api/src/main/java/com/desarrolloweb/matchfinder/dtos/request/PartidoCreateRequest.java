package com.desarrolloweb.matchfinder.dtos.request;

import jakarta.validation.constraints.NotNull;

public record PartidoCreateRequest(
        @NotNull Integer equipoUnoId,
        @NotNull Integer equipoDosId,
        @NotNull Integer ganadorId
) { }
