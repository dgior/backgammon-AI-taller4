package backgammon.game;

import backgammon.model.EstadoJuego;
import backgammon.model.Jugada;
import backgammon.model.Constantes;
import backgammon.ai.MinMax;
import java.util.List;
import java.util.Scanner;

public class JuegoBackgammon {
    private EstadoJuego estado;
    private MinMax ia;
    private Scanner scanner;

    public JuegoBackgammon() {
        this.estado = new EstadoJuego();
        this.ia = new MinMax();
        this.scanner = new Scanner(System.in);

        System.out.println("=== VERIFICANDO CONFIGURACIÓN INICIAL ===");
        if (!estado.verificarTotalFichas()) {
            System.out.println("¡ERROR! No hay 15 fichas por jugador");
        } else {
            System.out.println("✓ Configuración correcta: 15 fichas cada uno");
        }
        pausa(2000);
    }

    public void jugar() {
        System.out.println("\n=== BACKGAMMON - IA vs HUMANO ===");
        System.out.println("IA: ⚪ BLANCAS | Humano: ⚫ NEGRAS");
        System.out.println("Casillas del 0 al 23");
        pausa(2000);

        while (!estado.juegoTerminado()) {
            mostrarTablero();
            pausa(1000);

            if (estado.isTurnoIA()) {
                turnoIA();
            } else {
                turnoHumano();
            }

            estado.setTurnoIA(!estado.isTurnoIA());
            pausa(1500);
        }

        mostrarTablero();
        System.out.println("\n=== FIN DEL JUEGO ===");
        System.out.println("🏆 Ganador: " + estado.getGanador());
    }

    private void turnoIA() {
        System.out.println("\n🤖 TURNO DE IA (⚪ BLANCAS)");
        pausa(1000);

        int dado = lanzarDado();
        estado.setDadoAct(dado);
        System.out.println("🎲 Dado: " + dado);
        pausa(1000);

        System.out.print("🤖 Pensando");
        for (int i = 0; i < 3; i++) {
            pausa(500);
            System.out.print(".");
        }
        System.out.println();

        Jugada mejorMovimiento = ia.buscarMejorMovimiento(estado);

        if (mejorMovimiento == null) {
            System.out.println("❌ IA no tiene movimientos. Pierde turno.");
            pausa(2000);
            return;
        }

        System.out.println("🤖 Juega: " + mejorMovimiento);
        pausa(1500);

        ReglasBackgammon.aplicarMovimiento(estado, mejorMovimiento);

        if (mejorMovimiento.isEsCaptura()) {
            System.out.println("⚡ ¡CAPTURA!");
            pausa(1000);
        }

        System.out.println("\n✅ Turno de IA completado");
        pausa(1500);
    }

    private void turnoHumano() {
        System.out.println("\n👤 TURNO HUMANO (⚫ NEGRAS)");
        pausa(1000);

        int dado = lanzarDado();
        estado.setDadoAct(dado);
        System.out.println("🎲 Dado: " + dado);
        pausa(1000);

        List<Jugada> movimientos = MovimientosValidos.generarMovimientos(estado);

        if (movimientos.isEmpty()) {
            System.out.println("❌ No hay movimientos. Pierdes turno.");
            pausa(2000);
            return;
        }

        System.out.println("\n📋 TUS OPCIONES:");
        for (int i = 0; i < movimientos.size(); i++) {
            System.out.println("   " + (i+1) + ". " + movimientos.get(i));
            pausa(300);
        }

        int opcion = -1;
        while (opcion < 1 || opcion > movimientos.size()) {
            System.out.print("\n👉 Elige (1-" + movimientos.size() + "): ");
            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("❌ Número inválido");
            }
        }

        Jugada movimiento = movimientos.get(opcion - 1);
        System.out.println("▶️ Ejecutando: " + movimiento);
        pausa(1500);

        ReglasBackgammon.aplicarMovimiento(estado, movimiento);

        if (movimiento.isEsCaptura()) {
            System.out.println("⚡ ¡CAPTURASTE!");
            pausa(1000);
        }

        System.out.println("\n✅ Turno completado");
        pausa(1500);
    }

    private int lanzarDado() {
        return (int)(Math.random() * 6) + 1;
    }

    private void pausa(int milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            // Ignorar
        }
    }

    private void mostrarTablero() {
        int[] t = estado.getTablero();

        System.out.println("\n" + "═".repeat(90));
        System.out.println("                              T A B L E R O");
        System.out.println("═".repeat(90));

        // Información superior
        System.out.printf("   FUERA: ⚪ %2d   |   ⚫ %2d      CAPTURADAS: ⚪ %2d  ⚫ %2d%n",
                estado.getFichasTIA(), estado.getFichasTHum(),
                estado.getFichasCapHum(), estado.getFichasCapIA());
        System.out.println("─".repeat(90));

        // Fila superior (12-23) - Números de casilla
        System.out.println("\n       12     13     14     15     16     17    │   18     19     20     21     22     23");
        System.out.print("     ");

        // Casillas 12-17
        for (int i = 12; i <= 17; i++) {
            System.out.print("  " + formatearCasilla(t[i]) + "   ");
        }
        System.out.print("   │   ");

        // Casillas 18-23
        for (int i = 18; i <= 23; i++) {
            System.out.print("  " + formatearCasilla(t[i]) + "   ");
        }
        System.out.println("    ← SALIDA IA");

        // Barra
        System.out.println("\n    ───────────────────────────────────────────┼───────────────────────────────────────────");
        System.out.println("                    B A R R A                    │                 B A R R A");
        System.out.println("    ───────────────────────────────────────────┼───────────────────────────────────────────");

        // Fila inferior (11-0) - Números de casilla
        System.out.println("\n       11     10     9      8      7      6    │   5      4      3      2      1      0");
        System.out.print("     ");

        // Casillas 11-6
        for (int i = 11; i >= 6; i--) {
            System.out.print("  " + formatearCasilla(t[i]) + "   ");
        }
        System.out.print("   │   ");

        // Casillas 5-0
        for (int i = 5; i >= 0; i--) {
            System.out.print("  " + formatearCasilla(t[i]) + "   ");
        }
        System.out.println("    ← SALIDA HUMANO");

        System.out.println("─".repeat(90));
        System.out.println("   " + estado);
        System.out.println("═".repeat(90));
    }

    private String formatearCasilla(int valor) {
        if (valor > 0) {
            // Fichas blancas (IA)
            if (valor < 10) {
                return "⚪ " + valor + " ";
            } else {
                return "⚪" + valor + " ";
            }
        } else if (valor < 0) {
            // Fichas negras (Humano)
            int num = -valor;
            if (num < 10) {
                return "⚫ " + num + " ";
            } else {
                return "⚫" + num + " ";
            }
        } else {
            // Casilla vacía - usar puntos para mantener alineación
            return " ·  ";
        }
    }

    public static void main(String[] args) {
        JuegoBackgammon juego = new JuegoBackgammon();
        juego.jugar();
    }
}