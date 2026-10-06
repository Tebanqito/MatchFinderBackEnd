package com.desarrolloweb.matchfinder.controllers;

import com.desarrolloweb.matchfinder.dtos.request.PartidoCreateRequest;
import com.desarrolloweb.matchfinder.dtos.response.PartidoResponse;
import com.desarrolloweb.matchfinder.services.PartidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/torneos/{torneoId}/partidos")
@Validated
@RequiredArgsConstructor
public class PartidoController {

    private final PartidoService partidoService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<PartidoResponse> registrar(@PathVariable Integer torneoId,
                                                     @Valid @RequestBody PartidoCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partidoService.registrar(torneoId, request));
    }

    @GetMapping
    public ResponseEntity<List<PartidoResponse>> listar(@PathVariable Integer torneoId) {
        return ResponseEntity.ok(partidoService.listar(torneoId));
    }

    @DeleteMapping("/{partidoId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer torneoId, @PathVariable Integer partidoId) {
        partidoService.eliminar(torneoId, partidoId);
        return ResponseEntity.noContent().build();
    }
}
