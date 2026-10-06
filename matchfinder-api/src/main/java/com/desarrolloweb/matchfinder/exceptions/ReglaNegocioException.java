package com.desarrolloweb.matchfinder.exceptions;

/** 422: la petición es correcta pero incumple una regla de negocio. */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
