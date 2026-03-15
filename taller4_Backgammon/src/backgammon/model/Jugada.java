package backgammon.model;


public class Jugada {
    private int origen;
    private int destino;
    private int valorDado;
    private boolean esCaptura;
    private boolean esReingreso;
    private boolean esSalida;

    public Jugada(int origen, int destino, int valorDado) {
        this.origen = origen;
        this.destino = destino;
        this.valorDado = valorDado;
        this.esCaptura = false;
        this.esReingreso = (origen == -1); // -1 representa la barra
        this.esSalida = (destino == -1);   // -1 representa fuera
    }

    // Getters y setters
    public int getOrigen() { return origen; }
    public int getDestino() { return destino; }
    public int getValorDado() { return valorDado; }
    public boolean isEsCaptura() { return esCaptura; }
    public boolean isEsReingreso() { return esReingreso; }
    public boolean isEsSalida() { return esSalida; }

    public void setEsCaptura(boolean esCaptura) { this.esCaptura = esCaptura; }

    @Override
    public String toString() {
        if (esReingreso) {
            return "Reingresar desde barra a casilla " + destino + " (dado=" + valorDado + ")";
        } else if (esSalida) {
            return "Sacar ficha de casilla " + origen + " (dado=" + valorDado + ")";
        } else {
            return "Mover de " + origen + " a " + destino + " (dado=" + valorDado + ")" +
                    (esCaptura ? " ¡CAPTURA!" : "");
        }
    }
}