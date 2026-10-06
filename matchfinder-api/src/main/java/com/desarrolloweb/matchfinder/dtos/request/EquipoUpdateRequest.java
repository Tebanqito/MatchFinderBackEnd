package com.desarrolloweb.matchfinder.dtos.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Actualización parcial: los campos que se envían no pueden estar en blanco. */
public record EquipoUpdateRequest(
        @Size(max = 100) @Pattern(regexp = "(?s).*\\S.*", message = "no puede estar en blanco") String nombre,
        @Size(max = 500) @Pattern(regexp = "(?s).*\\S.*", message = "no puede estar en blanco") String descripcion
) { }
