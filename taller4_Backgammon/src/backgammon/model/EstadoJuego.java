package backgammon.model;

import java.util.Arrays;

public class EstadoJuego {
    private int[] tablero;
    private int fichasCapIA;
    private int fichasCapHum;
    private int fichasTIA;
    private int fichasTHum;
    private int dadoAct;
    private boolean turnoIA;

    public EstadoJuego() {
        this.tablero = new int[Constantes.NUM_CASILLAS];
        this.fichasCapIA = 0;
        this.fichasCapHum = 0;
        this.fichasTIA = 0;
        this.fichasTHum = 0;
        this.turnoIA = true; // IA empieza

        // LIMPIAR TABLERO PRIMERO
        Arrays.fill(tablero, 0);

        // CONFIGURACIÓN INICIAL CORRECTA (15 fichas cada uno)

        // Fichas de IA (BLANCAS) - valores POSITIVOS
        tablero[0] = 2;   // 2 fichas en casilla 0
        tablero[11] = 5;  // 5 fichas en casilla 11
        tablero[16] = 3;  // 3 fichas en casilla 16
        tablero[18] = 5;  // 5 fichas en casilla 18
        // Total IA: 2+5+3+5 = 15

        // Fichas de HUMANO (NEGRAS) - valores NEGATIVOS
        tablero[23] = -2;  // 2 fichas en casilla 23
        tablero[12] = -5;  // 5 fichas en casilla 12
        tablero[7] = -3;   // 3 fichas en casilla 7
        tablero[5] = -5;   // 5 fichas en casilla 5
        // Total Humano: 2+5+3+5 = 15
    }

    public EstadoJuego(EstadoJuego otro) {
        this.tablero = otro.tablero.clone();
        this.fichasCapIA = otro.fichasCapIA;
        this.fichasCapHum = otro.fichasCapHum;
        this.fichasTIA = otro.fichasTIA;
        this.fichasTHum = otro.fichasTHum;
        this.dadoAct = otro.dadoAct;
        this.turnoIA = otro.turnoIA;
    }

    // VERIFICAR que hay 15 fichas
    public boolean verificarTotalFichas() {
        int totalIA = 0;
        int totalHum = 0;

        for (int i = 0; i < Constantes.NUM_CASILLAS; i++) {
            if (tablero[i] > 0) totalIA += tablero[i];
            if (tablero[i] < 0) totalHum += -tablero[i];
        }

        totalIA += fichasTIA + fichasCapHum; // fichasCapHum = capturadas por humano (fichas IA en barra)
        totalHum += fichasTHum + fichasCapIA; // fichasCapIA = capturadas por IA (fichas Hum en barra)

        System.out.println("DEBUG - Total IA: " + totalIA + " | Total Hum: " + totalHum);
        return totalIA == 15 && totalHum == 15;
    }

    // Resto de getters y setters igual...
    public int[] getTablero() { return tablero; }
    public int getFichasCapIA() { return fichasCapIA; }
    public int getFichasCapHum() { return fichasCapHum; }
    public int getFichasTIA() { return fichasTIA; }
    public int getFichasTHum() { return fichasTHum; }
    public int getDadoAct() { return dadoAct; }
    public boolean isTurnoIA() { return turnoIA; }

    public void setFichasCapIA(int fichasCapIA) { this.fichasCapIA = fichasCapIA; }
    public void setFichasCapHum(int fichasCapHum) { this.fichasCapHum = fichasCapHum; }
    public void setFichasTIA(int fichasTIA) { this.fichasTIA = fichasTIA; }
    public void setFichasTHum(int fichasTHum) { this.fichasTHum = fichasTHum; }
    public void setDadoAct(int dadoAct) { this.dadoAct = dadoAct; }
    public void setTurnoIA(boolean turnoIA) { this.turnoIA = turnoIA; }

    public void setCasilla(int pos, int valor) {
        if (pos >= 0 && pos < Constantes.NUM_CASILLAS) {
            tablero[pos] = valor;
        }
    }

    public int getCasilla(int pos) {
        if (pos >= 0 && pos < Constantes.NUM_CASILLAS) {
            return tablero[pos];
        }
        return 0;
    }

    public boolean juegoTerminado() {
        return fichasTIA == Constantes.FICHAS_POR_JUGADOR ||
                fichasTHum == Constantes.FICHAS_POR_JUGADOR;
    }

    public String getGanador() {
        if (fichasTIA == Constantes.FICHAS_POR_JUGADOR) return "IA (⚪)";
        if (fichasTHum == Constantes.FICHAS_POR_JUGADOR) return "HUMANO (⚫)";
        return null;
    }

    @Override
    public String toString() {
        return String.format("Estado[IA:%d fuera,%d capturadas | Hum:%d fuera,%d capturadas | dado:%d | turno:%s]",
                fichasTIA, fichasCapHum, fichasTHum, fichasCapIA, dadoAct, turnoIA ? "IA" : "Humano");
    }
}