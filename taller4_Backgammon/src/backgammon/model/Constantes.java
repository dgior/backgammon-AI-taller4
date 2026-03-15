package backgammon.model;

public class Constantes {
    public static final int NUM_CASILLAS = 24;
    public static final int FICHAS_POR_JUGADOR = 15;
    public static final int DADO_MAX = 6;

    // Direcciones de movimiento
    public static final int DIR_IA = 1;        // IA mueve de 0 a 23
    public static final int DIR_HUMANO = -1;   // Humano mueve de 23 a 0

    // Pesos heurística
    public static final double W1 = 0.5; // progreso propio
    public static final double W2 = 0.3; // progreso rival
    public static final double W3 = 0.2; // capturas

    // Configuración inicial
    public static final int[][] CONFIG_INICIAL_IA = {
            {0, 2},   // casilla 0: 2 fichas
            {11, 5},  // casilla 11: 5 fichas
            {16, 3},  // casilla 16: 3 fichas
            {18, 5}   // casilla 18: 5 fichas
    };

    public static final int[][] CONFIG_INICIAL_HUMANO = {
            {23, 2},  // casilla 23: 2 fichas
            {12, 5},  // casilla 12: 5 fichas
            {7, 3},   // casilla 7: 3 fichas
            {5, 5}    // casilla 5: 5 fichas
    };
}
