package com.desarrolloweb.matchfinder.controllers;

import com.desarrolloweb.matchfinder.dtos.request.SolicitudAmistadCreateRequest;
import com.desarrolloweb.matchfinder.dtos.request.SolicitudAmistadRespuestaRequest;
import com.desarrolloweb.matchfinder.dtos.response.SolicitudAmistadResponse;
import com.desarrolloweb.matchfinder.entities.enums.EstadoSolicitud;
import com.desarrolloweb.matchfinder.services.AmistadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/solicitudes-amistad")
@Validated
@PreAuthorize("hasRole('USUARIO')")
@RequiredArgsConstructor
public class SolicitudAmistadController {

    private final AmistadService amistadService;

    /** 201 si queda pendiente; 200 si el otro ya había enviado una y se aceptó sola. */
    @PostMapping
    public ResponseEntity<SolicitudAmistadResponse> enviar(@Valid @RequestBody SolicitudAmistadCreateRequest request,
                                                           Authentication authentication) {
        SolicitudAmistadResponse respuesta =
                amistadService.enviarSolicitud(authentication.getName(), request.nombreDestinatario());
        HttpStatus status = respuesta.estado() == EstadoSolicitud.PENDIENTE ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(respuesta);
    }

    @GetMapping("/recibidas")
    public ResponseEntity<List<SolicitudAmistadResponse>> recibidas(
            @RequestParam(defaultValue = "PENDIENTE") EstadoSolicitud estado,
            Authentication authentication) {
        return ResponseEntity.ok(amistadService.solicitudesRecibidas(authentication.getName(), estado));
    }

    @GetMapping("/enviadas")
    public ResponseEntity<List<SolicitudAmistadResponse>> enviadas(Authentication authentication) {
        return ResponseEntity.ok(amistadService.solicitudesEnviadas(authentication.getName()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SolicitudAmistadResponse> responder(
            @PathVariable Integer id,
            @Valid @RequestBody SolicitudAmistadRespuestaRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(
                amistadService.responderSolicitud(authentication.getName(), id, request.estado()));
    }
}
