package com.desarrolloweb.matchfinder.dtos.request;

import com.desarrolloweb.matchfinder.entities.enums.TipoEquipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EquipoCreateRequest(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Size(max = 500) String descripcion,
        @NotNull TipoEquipo tipo
) { }
