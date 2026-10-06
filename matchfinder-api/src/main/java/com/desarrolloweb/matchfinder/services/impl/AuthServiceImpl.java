package com.desarrolloweb.matchfinder.services.impl;

import com.desarrolloweb.matchfinder.dtos.request.LoginRequest;
import com.desarrolloweb.matchfinder.dtos.request.RegistroRequest;
import com.desarrolloweb.matchfinder.dtos.response.AuthResponse;
import com.desarrolloweb.matchfinder.entities.MatchFinderUser;
import com.desarrolloweb.matchfinder.entities.enums.RolName;
import com.desarrolloweb.matchfinder.exceptions.ConflictoException;
import com.desarrolloweb.matchfinder.repositories.MatchFinderUserRepository;
import com.desarrolloweb.matchfinder.security.JwtService;
import com.desarrolloweb.matchfinder.security.OwnerKeyValidator;
import com.desarrolloweb.matchfinder.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MatchFinderUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final OwnerKeyValidator ownerKeyValidator;

    @Override
    @Transactional
    public AuthResponse registrar(RegistroRequest request) {
        // Todo registro público queda siempre con rol USUARIO
        return construirRespuesta(crearUsuario(request, RolName.USUARIO));
    }

    @Override
    @Transactional
    public AuthResponse registrarOwner(RegistroRequest request, String claveOwner) {
        if (!ownerKeyValidator.esValida(claveOwner)) {
            throw new AccessDeniedException("Clave de registro inválida");
        }
        if (userRepository.existsByRol(RolName.OWNER)) {
            throw new ConflictoException("Ya existe un owner en el sistema");
        }
        try {
            return construirRespuesta(crearUsuario(request, RolName.OWNER));
        } catch (DataIntegrityViolationException e) {
            // Dos altas simultáneas: la restricción única de la base de datos deja pasar solo una
            throw new ConflictoException("Ya existe un owner en el sistema");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password()));

        MatchFinderUser user = userRepository.findByEmail(email).orElseThrow();
        return construirRespuesta(user);
    }

    private MatchFinderUser crearUsuario(RegistroRequest request, RolName rol) {
        String nombre = request.nombre().trim();
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new ConflictoException("Ya existe un usuario registrado con ese email");
        }
        if (userRepository.existsByNombre(nombre)) {
            throw new ConflictoException("Ya existe un usuario con ese nombre");
        }

        return userRepository.saveAndFlush(MatchFinderUser.builder()
                .nombre(nombre)
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .rol(rol)
                .build());
    }

    private AuthResponse construirRespuesta(MatchFinderUser user) {
        String token = jwtService.generateToken(user.getEmail(), List.of("ROLE_" + user.getRol().name()));
        return new AuthResponse(token, "Bearer", user.getEmail(), user.getRol().name());
    }
}
