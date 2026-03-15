package backgammon.ai;

import backgammon.model.EstadoJuego;
import backgammon.model.Constantes;

public class Heuristica {

    // Calcula el valor heurístico de un estado (positivo = bueno para IA)
    public static double calcular(EstadoJuego estado) {
        // Ind1: Progreso de fichas propias (IA)
        double ind1 = calcularProgresoIA(estado);

        // Ind2: Progreso del oponente (Humano)
        double ind2 = calcularProgresoHumano(estado);

        // Ind3: Control del tablero / capturas
        double ind3 = estado.getFichasCapIA(); // Capturas hechas por IA

        // Normalizar valores para que estén en rangos similares
        ind1 = normalizar(ind1, 0, 200); // Máxima suma de posiciones ~200
        ind2 = normalizar(ind2, 0, 200);
        ind3 = normalizar(ind3, 0, 15);  // Máximo 15 capturas

        // Aplicar pesos
        return Constantes.W1 * ind1 - Constantes.W2 * ind2 + Constantes.W3 * ind3;
    }

    // Calcula qué tan cerca están las fichas de IA de salir
    private static double calcularProgresoIA(EstadoJuego estado) {
        double progreso = 0;
        int totalFichas = 0;

        // Sumar posiciones de fichas en tablero
        for (int i = 0; i < Constantes.NUM_CASILLAS; i++) {
            int fichas = estado.getTablero()[i];
            if (fichas > 0) { // Fichas de IA
                // Mientras más cerca de 23, mejor (menor distancia para salir)
                // 23 es la última casilla antes de salir
                progreso += fichas * (i + 1); // +1 para evitar multiplicar por 0
                totalFichas += fichas;
            }
        }

        // Sumar fichas fuera (ya terminaron)
        progreso += estado.getFichasTIA() * 25; // Las fichas fuera valen más que la última casilla

        return progreso;
    }

    // Calcula qué tan cerca están las fichas de Humano de salir (negativo para IA)
    private static double calcularProgresoHumano(EstadoJuego estado) {
        double progreso = 0;

        for (int i = 0; i < Constantes.NUM_CASILLAS; i++) {
            int fichas = estado.getTablero()[i];
            if (fichas < 0) { // Fichas de Humano
                // Para humano, la distancia se mide desde 23 hacia 0
                // Convertir a valor positivo: 23-i es la distancia a la salida
                int distanciaSalida = 23 - i;
                progreso += (-fichas) * (distanciaSalida + 1);
            }
        }

        // Sumar fichas fuera del humano
        progreso += estado.getFichasTHum() * 25;

        return progreso;
    }

    // Normaliza un valor entre 0 y 1
    private static double normalizar(double valor, double min, double max) {
        if (max == min) return 0.5;
        return (valor - min) / (max - min);
    }
}