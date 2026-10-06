package com.desarrolloweb.matchfinder.dtos.request;

import com.desarrolloweb.matchfinder.entities.enums.EstadoTorneo;
import jakarta.validation.constraints.NotNull;

public record TorneoUpdateRequest(
        @NotNull EstadoTorneo estado
) { }
