package com.desarrolloweb.matchfinder.dtos.request;

import com.desarrolloweb.matchfinder.entities.enums.TipoEquipo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TorneoCreateRequest(
        @NotBlank @Size(max = 100) String nombre,
        @NotNull TipoEquipo tipo,
        @NotNull @Min(2) Integer cantidadEquipos
) { }
