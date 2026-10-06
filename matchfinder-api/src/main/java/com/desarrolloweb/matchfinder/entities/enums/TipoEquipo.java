package com.desarrolloweb.matchfinder.entities.enums;

public enum TipoEquipo {
    FUTBOL_5(5),
    FUTBOL_11(11);

    private final int cupo;

    TipoEquipo(int cupo) {
        this.cupo = cupo;
    }

    /** Cantidad total de integrantes, contando al creador. */
    public int getCupo() {
        return cupo;
    }
}
