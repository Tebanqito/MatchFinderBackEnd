package com.desarrolloweb.matchfinder.controllers;

import com.desarrolloweb.matchfinder.dtos.request.EquipoCreateRequest;
import com.desarrolloweb.matchfinder.dtos.request.EquipoUpdateRequest;
import com.desarrolloweb.matchfinder.dtos.response.EquipoDetalleResponse;
import com.desarrolloweb.matchfinder.dtos.response.EquipoResponse;
import com.desarrolloweb.matchfinder.dtos.response.PaginaResponse;
import com.desarrolloweb.matchfinder.entities.enums.TipoEquipo;
import com.desarrolloweb.matchfinder.services.EquipoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/equipos")
@Validated
@RequiredArgsConstructor
public class EquipoController {

    private final EquipoService equipoService;

    @PostMapping
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<EquipoDetalleResponse> crear(@Valid @RequestBody EquipoCreateRequest request,
                                                       Authentication authentication,
                                                       UriComponentsBuilder uriBuilder) {
        EquipoDetalleResponse creado = equipoService.crear(request, authentication.getName());
        URI location = uriBuilder.path("/api/v1/equipos/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.status(HttpStatus.CREATED).location(location).body(creado);
    }

    @GetMapping
    public ResponseEntity<PaginaResponse<EquipoResponse>> listar(
            @RequestParam(required = false) TipoEquipo tipo,
            @RequestParam(required = false) String nombre,
            @RequestParam(defaultValue = "false") boolean conCupo,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return ResponseEntity.ok(equipoService.listar(tipo, nombre, conCupo, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipoDetalleResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(equipoService.obtener(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<EquipoDetalleResponse> editar(@PathVariable Integer id,
                                                        @Valid @RequestBody EquipoUpdateRequest request,
                                                        Authentication authentication) {
        return ResponseEntity.ok(equipoService.editar(id, request, authentication.getName()));
    }

    @PostMapping("/{id}/miembros")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<EquipoDetalleResponse> unirse(@PathVariable Integer id,
                                                        Authentication authentication) {
        return ResponseEntity.ok(equipoService.unirse(id, authentication.getName()));
    }

    @PostMapping("/{id}/miembros/{usuarioId}")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<EquipoDetalleResponse> agregarAmigo(@PathVariable Integer id,
                                                              @PathVariable Integer usuarioId,
                                                              Authentication authentication) {
        return ResponseEntity.ok(equipoService.agregarAmigo(id, usuarioId, authentication.getName()));
    }

    @DeleteMapping("/{id}/miembros/me")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<Void> abandonar(@PathVariable Integer id, Authentication authentication) {
        equipoService.abandonar(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        equipoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
