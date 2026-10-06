package com.desarrolloweb.matchfinder.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SolicitudAmistadCreateRequest(
        @NotBlank @Size(max = 50) String nombreDestinatario
) { }
