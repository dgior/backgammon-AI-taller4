package backgammon.ai;

import backgammon.model.EstadoJuego;
import backgammon.model.Jugada;
import backgammon.game.MovimientosValidos;
import backgammon.game.ReglasBackgammon;
import java.util.List;

public class MinMax {
    private static final int PROFUNDIDAD = 2;
    private MejorMovimiento mejorMovimiento;

    public Jugada buscarMejorMovimiento(EstadoJuego estado) {
        mejorMovimiento = new MejorMovimiento();
        mejorMovimiento.valor = Double.NEGATIVE_INFINITY;

        // MAX (IA) - nivel 0
        List<Jugada> movimientos = MovimientosValidos.generarMovimientos(estado);

        if (movimientos.isEmpty()) {
            return null; // No hay movimientos válidos
        }

        for (Jugada movimiento : movimientos) {
            EstadoJuego nuevoEstado = new EstadoJuego(estado);
            ReglasBackgammon.aplicarMovimiento(nuevoEstado, movimiento);

            // Cambiar turno para el siguiente nivel
            nuevoEstado.setTurnoIA(!nuevoEstado.isTurnoIA());

            double valorMovimiento = min(nuevoEstado, PROFUNDIDAD - 1);

            if (valorMovimiento > mejorMovimiento.valor) {
                mejorMovimiento.valor = valorMovimiento;
                mejorMovimiento.jugada = movimiento;
            }
        }

        return mejorMovimiento.jugada;
    }

    private double max(EstadoJuego estado, int profundidad) {
        if (profundidad == 0 || estado.juegoTerminado()) {
            return Heuristica.calcular(estado);
        }

        double maxValor = Double.NEGATIVE_INFINITY;
        List<Jugada> movimientos = MovimientosValidos.generarMovimientos(estado);

        // Si no hay movimientos, evaluar estado actual
        if (movimientos.isEmpty()) {
            return Heuristica.calcular(estado);
        }

        for (Jugada movimiento : movimientos) {
            EstadoJuego nuevoEstado = new EstadoJuego(estado);
            ReglasBackgammon.aplicarMovimiento(nuevoEstado, movimiento);
            nuevoEstado.setTurnoIA(!nuevoEstado.isTurnoIA());

            double valor = min(nuevoEstado, profundidad - 1);
            maxValor = Math.max(maxValor, valor);
        }

        return maxValor;
    }

    private double min(EstadoJuego estado, int profundidad) {
        if (profundidad == 0 || estado.juegoTerminado()) {
            return Heuristica.calcular(estado);
        }

        double minValor = Double.POSITIVE_INFINITY;
        List<Jugada> movimientos = MovimientosValidos.generarMovimientos(estado);

        // Si no hay movimientos, evaluar estado actual
        if (movimientos.isEmpty()) {
            return Heuristica.calcular(estado);
        }

        for (Jugada movimiento : movimientos) {
            EstadoJuego nuevoEstado = new EstadoJuego(estado);
            ReglasBackgammon.aplicarMovimiento(nuevoEstado, movimiento);
            nuevoEstado.setTurnoIA(!nuevoEstado.isTurnoIA());

            double valor = max(nuevoEstado, profundidad - 1);
            minValor = Math.min(minValor, valor);
        }

        return minValor;
    }

    // Clase auxiliar para guardar el mejor movimiento
    private class MejorMovimiento {
        Jugada jugada;
        double valor;
    }
}