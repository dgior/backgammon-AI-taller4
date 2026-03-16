package backgammon.game;

import backgammon.model.EstadoJuego;
import backgammon.model.Jugada;
import backgammon.model.Constantes;
import java.util.ArrayList;
import java.util.List;

public class ReglasBackgammon {

    // Verifica si un movimiento es válido
    public static boolean esMovimientoValido(EstadoJuego estado, Jugada jugada) {
        int origen = jugada.getOrigen();
        int destino = jugada.getDestino();
        int dado = jugada.getValorDado();
        boolean turnoIA = estado.isTurnoIA();

        // Caso especial: reingreso desde barra
        if (jugada.isEsReingreso()) {
            return puedeReingresar(estado, destino, turnoIA);
        }

        // Caso especial: sacar ficha
        if (jugada.isEsSalida()) {
            return puedeSacarFicha(estado, origen, turnoIA);
        }

        // Validaciones básicas
        if (origen < 0 || origen >= Constantes.NUM_CASILLAS) return false;
        if (destino < 0 || destino >= Constantes.NUM_CASILLAS) return false;

        // Verificar que hay ficha propia en origen
        int fichaEnOrigen = estado.getCasilla(origen);
        if (turnoIA && fichaEnOrigen <= 0) return false;
        if (!turnoIA && fichaEnOrigen >= 0) return false;

        // Verificar que el destino es alcanzable con el dado
        int direccion = turnoIA ? Constantes.DIR_IA : Constantes.DIR_HUMANO;
        if (destino != origen + (dado * direccion)) return false;

        // Verificar que el destino no está bloqueado (2 o más fichas rivales)
        int fichasEnDestino = estado.getCasilla(destino);
        if (turnoIA && fichasEnDestino < -1) return false; // 2 o más fichas negras bloquean
        if (!turnoIA && fichasEnDestino > 1) return false; // 2 o más fichas blancas bloquean

        // Si hay 1 ficha rival, es capturable (válido)
        return true;
    }

    // Verifica si se puede reingresar desde la barra
    private static boolean puedeReingresar(EstadoJuego estado, int destino, boolean turnoIA) {
        // Verificar que hay fichas en barra
        if (turnoIA && estado.getFichasCapHum() == 0) return false;
        if (!turnoIA && estado.getFichasCapIA() == 0) return false;

        // Verificar que la casilla destino no está bloqueada
        int fichasEnDestino = estado.getCasilla(destino);
        if (turnoIA && fichasEnDestino < -1) return false; // Bloqueada por humano
        if (!turnoIA && fichasEnDestino > 1) return false; // Bloqueada por IA

        return true;
    }

    // Verifica si se puede sacar una ficha
    private static boolean puedeSacarFicha(EstadoJuego estado, int origen, boolean turnoIA) {
        // Solo se puede sacar si todas las fichas están en el cuadrante final
        if (!todasFichasEnCuadranteFinal(estado, turnoIA)) return false;

        int fichasEnOrigen = estado.getCasilla(origen);
        if (turnoIA && fichasEnOrigen <= 0) return false;
        if (!turnoIA && fichasEnOrigen >= 0) return false;

        return true;
    }

    // Verifica si todas las fichas del jugador están en su cuadrante final
    private static boolean todasFichasEnCuadranteFinal(EstadoJuego estado, boolean turnoIA) {
        int inicioCuadrante = turnoIA ? 18 : 0; // IA: casillas 18-23, Humano: 0-5
        int finCuadrante = turnoIA ? 23 : 5;

        for (int i = 0; i < Constantes.NUM_CASILLAS; i++) {
            int fichas = estado.getCasilla(i);
            if (turnoIA && fichas > 0) { // Fichas de IA
                if (i < inicioCuadrante || i > finCuadrante) return false;
            } else if (!turnoIA && fichas < 0) { // Fichas de humano
                if (i < inicioCuadrante || i > finCuadrante) return false;
            }
        }
        return true;
    }

    // Aplica un movimiento al estado
    public static void aplicarMovimiento(EstadoJuego estado, Jugada jugada) {
        int origen = jugada.getOrigen();
        int destino = jugada.getDestino();
        boolean turnoIA = estado.isTurnoIA();

        if (jugada.isEsReingreso()) {
            // Reingresar desde barra
            if (turnoIA) {
                estado.setFichasCapHum(estado.getFichasCapHum() - 1);
                estado.setCasilla(destino, estado.getCasilla(destino) + 1);
            } else {
                estado.setFichasCapIA(estado.getFichasCapIA() - 1);
                estado.setCasilla(destino, estado.getCasilla(destino) - 1);
            }
        } else if (jugada.isEsSalida()) {
            // Sacar ficha
            if (turnoIA) {
                estado.setCasilla(origen, estado.getCasilla(origen) - 1);
                estado.setFichasTIA(estado.getFichasTIA() + 1);
            } else {
                estado.setCasilla(origen, estado.getCasilla(origen) + 1);
                estado.setFichasTHum(estado.getFichasTHum() + 1);
            }
        } else {
            // Movimiento normal
            int fichasOrigen = estado.getCasilla(origen);
            int fichasDestino = estado.getCasilla(destino);

            // RESTAR FICHA DEL ORIGEN PRIMERO
            if (turnoIA) {
                estado.setCasilla(origen, fichasOrigen - 1);
            } else {
                estado.setCasilla(origen, fichasOrigen + 1); // +1 porque es negativo
            }

            // VERIFICAR CAPTURA ANTES DE SUMAR AL DESTINO
            if (turnoIA && fichasDestino == -1) {
                // IA captura ficha humana (una sola ficha negra)
                // System.out.println("DEBUG - IA captura ficha humana en casilla " + destino);
                estado.setFichasCapIA(estado.getFichasCapIA() + 1);
                jugada.setEsCaptura(true);
                // La ficha humana desaparece, se pone la ficha IA
                estado.setCasilla(destino, 1);
            } else if (!turnoIA && fichasDestino == 1) {
                // Humano captura ficha IA (una sola ficha blanca)
                // System.out.println("DEBUG - Humano captura ficha IA en casilla " + destino);
                estado.setFichasCapHum(estado.getFichasCapHum() + 1);
                jugada.setEsCaptura(true);
                // La ficha IA desaparece, se pone la ficha humana
                estado.setCasilla(destino, -1);
            } else {
                // Movimiento normal sin captura
                if (turnoIA) {
                    estado.setCasilla(destino, fichasDestino + 1);
                } else {
                    estado.setCasilla(destino, fichasDestino - 1);
                }
            }
        }
    }
}