package backgammon.game;

import backgammon.model.EstadoJuego;
import backgammon.model.Jugada;
import backgammon.model.Constantes;
import java.util.ArrayList;
import java.util.List;

public class MovimientosValidos {

    // Genera todos los movimientos posibles para el estado actual
    public static List<Jugada> generarMovimientos(EstadoJuego estado) {
        List<Jugada> movimientos = new ArrayList<>();
        boolean turnoIA = estado.isTurnoIA();
        int dado = estado.getDadoAct();

        // Primero verificar si hay fichas en barra
        if (turnoIA && estado.getFichasCapHum() > 0) {
            generarReingresos(estado, movimientos);
            return movimientos; // Si hay fichas en barra, solo se puede reingresar
        } else if (!turnoIA && estado.getFichasCapIA() > 0) {
            generarReingresos(estado, movimientos);
            return movimientos; // Si hay fichas en barra, solo se puede reingresar
        }

        // Verificar si puede sacar fichas
        if (puedeSacarFichas(estado, turnoIA)) {
            generarSalidas(estado, movimientos);
        }

        // Generar movimientos normales
        generarMovimientosNormales(estado, movimientos);

        return movimientos;
    }

    private static void generarReingresos(EstadoJuego estado, List<Jugada> movimientos) {
        boolean turnoIA = estado.isTurnoIA();
        int dado = estado.getDadoAct();
        int destino;

        if (turnoIA) {
            // IA reingresa en casilla dado-1 (considerando que la barra está en posición -1)
            destino = dado - 1; // Si dado=1, destino=0; dado=2, destino=1; etc.
            if (destino >= 0 && destino < Constantes.NUM_CASILLAS) {
                Jugada jugada = new Jugada(-1, destino, dado);
                if (ReglasBackgammon.esMovimientoValido(estado, jugada)) {
                    movimientos.add(jugada);
                }
            }
        } else {
            // Humano reingresa en casilla 24-dado
            destino = 24 - dado;
            if (destino >= 0 && destino < Constantes.NUM_CASILLAS) {
                Jugada jugada = new Jugada(-1, destino, dado);
                if (ReglasBackgammon.esMovimientoValido(estado, jugada)) {
                    movimientos.add(jugada);
                }
            }
        }
    }

    private static void generarSalidas(EstadoJuego estado, List<Jugada> movimientos) {
        boolean turnoIA = estado.isTurnoIA();
        int dado = estado.getDadoAct();

        if (turnoIA) {
            // IA saca de casilla 18-23
            int casillaSalida = 18 + (dado - 1);
            if (casillaSalida <= 23 && estado.getCasilla(casillaSalida) > 0) {
                Jugada jugada = new Jugada(casillaSalida, -1, dado);
                if (ReglasBackgammon.esMovimientoValido(estado, jugada)) {
                    movimientos.add(jugada);
                }
            }
        } else {
            // Humano saca de casilla 5-0
            int casillaSalida = 5 - (dado - 1);
            if (casillaSalida >= 0 && estado.getCasilla(casillaSalida) < 0) {
                Jugada jugada = new Jugada(casillaSalida, -1, dado);
                if (ReglasBackgammon.esMovimientoValido(estado, jugada)) {
                    movimientos.add(jugada);
                }
            }
        }
    }

    private static void generarMovimientosNormales(EstadoJuego estado, List<Jugada> movimientos) {
        boolean turnoIA = estado.isTurnoIA();
        int dado = estado.getDadoAct();
        int direccion = turnoIA ? Constantes.DIR_IA : Constantes.DIR_HUMANO;

        for (int i = 0; i < Constantes.NUM_CASILLAS; i++) {
            int fichas = estado.getCasilla(i);

            // Verificar si hay ficha propia en esta casilla
            if ((turnoIA && fichas > 0) || (!turnoIA && fichas < 0)) {
                int destino = i + (dado * direccion);

                // Verificar que el destino está dentro del tablero
                if (destino >= 0 && destino < Constantes.NUM_CASILLAS) {
                    Jugada jugada = new Jugada(i, destino, dado);
                    if (ReglasBackgammon.esMovimientoValido(estado, jugada)) {
                        movimientos.add(jugada);
                    }
                }
            }
        }
    }

    private static boolean puedeSacarFichas(EstadoJuego estado, boolean turnoIA) {
        int inicioCuadrante = turnoIA ? 18 : 0;
        int finCuadrante = turnoIA ? 23 : 5;

        // Verificar que no hay fichas fuera del cuadrante final
        for (int i = 0; i < Constantes.NUM_CASILLAS; i++) {
            int fichas = estado.getCasilla(i);
            if (turnoIA && fichas > 0) {
                if (i < inicioCuadrante || i > finCuadrante) return false;
            } else if (!turnoIA && fichas < 0) {
                if (i < inicioCuadrante || i > finCuadrante) return false;
            }
        }
        return true;
    }
}