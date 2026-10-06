package com.desarrolloweb.matchfinder.exceptions;

/** 409: la petición choca con el estado actual del sistema. */
public class ConflictoException extends RuntimeException {

    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
