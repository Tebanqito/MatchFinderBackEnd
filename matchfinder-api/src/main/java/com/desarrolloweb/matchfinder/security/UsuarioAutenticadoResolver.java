package com.desarrolloweb.matchfinder.security;

import com.desarrolloweb.matchfinder.entities.MatchFinderUser;
import com.desarrolloweb.matchfinder.repositories.MatchFinderUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Component;

/** Obtiene la entidad del usuario a partir del email que viene en el token. */
@Component
@RequiredArgsConstructor
public class UsuarioAutenticadoResolver {

    private final MatchFinderUserRepository userRepository;

    public MatchFinderUser obtener(String email) {
        // Si el usuario fue eliminado, su token sigue siendo válido hasta que expire: se responde 401
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new InsufficientAuthenticationException("El usuario autenticado ya no existe"));
    }
}
