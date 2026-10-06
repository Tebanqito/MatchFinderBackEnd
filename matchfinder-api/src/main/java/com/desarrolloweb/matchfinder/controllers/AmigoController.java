package com.desarrolloweb.matchfinder.controllers;

import com.desarrolloweb.matchfinder.dtos.response.UsuarioResumenResponse;
import com.desarrolloweb.matchfinder.services.AmistadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/amigos")
@Validated
@PreAuthorize("hasRole('USUARIO')")
@RequiredArgsConstructor
public class AmigoController {

    private final AmistadService amistadService;

    @GetMapping
    public ResponseEntity<List<UsuarioResumenResponse>> listar(Authentication authentication) {
        return ResponseEntity.ok(amistadService.listarAmigos(authentication.getName()));
    }

    @DeleteMapping("/{amigoId}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer amigoId, Authentication authentication) {
        amistadService.eliminarAmigo(authentication.getName(), amigoId);
        return ResponseEntity.noContent().build();
    }
}
