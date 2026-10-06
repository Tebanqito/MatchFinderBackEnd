package com.desarrolloweb.matchfinder.controllers;

import com.desarrolloweb.matchfinder.dtos.request.TorneoCreateRequest;
import com.desarrolloweb.matchfinder.dtos.request.TorneoUpdateRequest;
import com.desarrolloweb.matchfinder.dtos.response.TorneoResponse;
import com.desarrolloweb.matchfinder.services.TorneoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/torneos")
@Validated
@RequiredArgsConstructor
public class TorneoController {

    private final TorneoService torneoService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<TorneoResponse> crear(@Valid @RequestBody TorneoCreateRequest request,
                                                UriComponentsBuilder uriBuilder) {
        TorneoResponse creado = torneoService.crear(request);
        URI location = uriBuilder.path("/api/v1/torneos/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.status(HttpStatus.CREATED).location(location).body(creado);
    }

    @GetMapping("/activo")
    public ResponseEntity<TorneoResponse> obtenerActivo() {
        return ResponseEntity.ok(torneoService.obtenerActivo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TorneoResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(torneoService.obtener(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<TorneoResponse> actualizarEstado(@PathVariable Integer id,
                                                           @Valid @RequestBody TorneoUpdateRequest request) {
        return ResponseEntity.ok(torneoService.actualizarEstado(id, request.estado()));
    }
}
