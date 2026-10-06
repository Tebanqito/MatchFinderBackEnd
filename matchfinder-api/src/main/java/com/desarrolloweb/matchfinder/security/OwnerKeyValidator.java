package com.desarrolloweb.matchfinder.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Valida la clave secreta con la que se da de alta al owner.
 * Si la clave no está configurada o tiene menos de 32 caracteres, el alta queda deshabilitada.
 * La comparación se hace sobre hashes y en tiempo constante.
 */
@Component
public class OwnerKeyValidator {

    private static final Logger log = LoggerFactory.getLogger(OwnerKeyValidator.class);
    private static final int LONGITUD_MINIMA = 32;

    private final byte[] claveHash;

    public OwnerKeyValidator(@Value("${app.owner.registration-key:}") String clave) {
        if (clave == null || clave.length() < LONGITUD_MINIMA) {
            this.claveHash = null;
            log.warn("OWNER_REGISTRATION_KEY no está configurada o tiene menos de {} caracteres: "
                    + "el alta del owner está deshabilitada", LONGITUD_MINIMA);
        } else {
            this.claveHash = sha256(clave);
        }
    }

    public boolean esValida(String claveRecibida) {
        if (claveHash == null || claveRecibida == null) {
            return false;
        }
        return MessageDigest.isEqual(claveHash, sha256(claveRecibida));
    }

    private static byte[] sha256(String valor) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(valor.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
