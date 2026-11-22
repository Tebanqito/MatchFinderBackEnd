package com.desarrolloweb.matchfinder.services.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * Calcula la llave de eliminación directa de un torneo a partir del orden del sorteo y de los resultados cargados.
 * Si la cantidad de equipos no es potencia de 2, los primeros sorteados pasan directo a la ronda siguiente.
 */
public final class LlaveCalculador {

    private LlaveCalculador() { }

    /** Un partido ya cargado. */
    public record Resultado(Integer partidoId, Integer equipoUno, Integer equipoDos, Integer ganador) { }

    /** Un cruce de la llave. Los equipos son null mientras no se sepa quién llega. */
    public record Cruce(Integer equipoUno, Integer equipoDos, boolean paseDirecto, Integer partidoId, Integer ganador) { }

    public static List<List<Cruce>> calcular(List<Integer> equipos, List<Resultado> resultados) {
        int tamanio = 2;
        while (tamanio < equipos.size()) {
            tamanio *= 2;
        }

        List<Integer> actual = new ArrayList<>();
        for (int semilla : semillas(tamanio)) {
            actual.add(semilla <= equipos.size() ? equipos.get(semilla - 1) : null);
        }

        List<List<Cruce>> rondas = new ArrayList<>();
        for (int k = 0; actual.size() > 1; k++) {
            List<Cruce> ronda = new ArrayList<>();
            List<Integer> siguiente = new ArrayList<>();
            for (int i = 0; i < actual.size(); i += 2) {
                Integer a = actual.get(i);
                Integer b = actual.get(i + 1);
                Integer ganador = null;
                Integer partidoId = null;
                boolean pase = false;

                if (a != null && b != null) {
                    Resultado r = buscar(resultados, a, b);
                    if (r != null) {
                        partidoId = r.partidoId();
                        ganador = r.ganador();
                    }
                } else if (k == 0) {
                    ganador = a != null ? a : b; // pase directo: solo existe en la primera ronda
                    pase = true;
                }

                ronda.add(new Cruce(a, b, pase, partidoId, ganador));
                siguiente.add(ganador);
            }
            rondas.add(ronda);
            actual = siguiente;
        }
        return rondas;
    }

    /** Ganador de la final, o null si todavía no se definió. */
    public static Integer campeon(List<List<Cruce>> rondas) {
        return rondas.isEmpty() ? null : rondas.get(rondas.size() - 1).get(0).ganador();
    }

    public static String nombreRonda(int indice, int totalRondas) {
        return switch (totalRondas - indice) {
            case 1 -> "Final";
            case 2 -> "Semifinales";
            case 3 -> "Cuartos de final";
            case 4 -> "Octavos de final";
            default -> "Ronda " + (indice + 1);
        };
    }

    /** Ids de los partidos de rondas posteriores que dependen del ganador del partido indicado. */
    public static List<Integer> partidosDependientes(List<List<Cruce>> rondas, Integer partidoId) {
        List<Integer> dependientes = new ArrayList<>();
        for (int k = 0; k < rondas.size(); k++) {
            for (int i = 0; i < rondas.get(k).size(); i++) {
                if (partidoId.equals(rondas.get(k).get(i).partidoId())) {
                    int posicion = i;
                    for (int r = k + 1; r < rondas.size(); r++) {
                        posicion /= 2;
                        Integer posterior = rondas.get(r).get(posicion).partidoId();
                        if (posterior != null) {
                            dependientes.add(posterior);
                        }
                    }
                    return dependientes;
                }
            }
        }
        return dependientes;
    }

    /** Orden de las semillas en la llave: con 8 lugares queda 1-8, 4-5, 2-7, 3-6. */
    private static List<Integer> semillas(int n) {
        List<Integer> a = new ArrayList<>(List.of(1));
        while (a.size() < n) {
            int m = a.size() * 2 + 1;
            List<Integer> nuevo = new ArrayList<>();
            for (int x : a) {
                nuevo.add(x);
                nuevo.add(m - x);
            }
            a = nuevo;
        }
        return a;
    }

    private static Resultado buscar(List<Resultado> resultados, Integer a, Integer b) {
        for (Resultado r : resultados) {
            if ((r.equipoUno().equals(a) && r.equipoDos().equals(b))
                    || (r.equipoUno().equals(b) && r.equipoDos().equals(a))) {
                return r;
            }
        }
        return null;
    }
}
