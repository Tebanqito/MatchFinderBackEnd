package com.desarrolloweb.matchfinder.dtos.request;

import com.desarrolloweb.matchfinder.entities.enums.EstadoSolicitud;
import jakarta.validation.constraints.NotNull;

public record SolicitudAmistadRespuestaRequest(
        @NotNull EstadoSolicitud estado
) { }
