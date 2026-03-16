package backgammon.game;

import backgammon.model.EstadoJuego;
import backgammon.model.Jugada;
import backgammon.model.Constantes;
import backgammon.ai.MinMax;
import java.util.List;
import java.util.Scanner;

public class JuegoBackgammon {
    private EstadoJuego estado;      // Representa el estado actual del tablero
    private MinMax ia;                // Algoritmo de IA para decidir movimientos
    private Scanner scanner;          // Lee entrada del usuario

    public JuegoBackgammon() {
        this.estado = new EstadoJuego();     // Inicializa el tablero con la configuración inicial
        this.ia = new MinMax();               // Prepara la IA
        this.scanner = new Scanner(System.in); // Prepara entrada de datos

        System.out.println("verificando configuracion inicial");
        if (!estado.verificarTotalFichas()) {  // Comprueba que hay exactamente 15 fichas por jugador
            System.out.println("Ha ocurrido un error, no hay 15 fichas por jugador");
        } else {
            System.out.println("La Configuracion es correcta, hay 15 fichas por cada jugador");
        }
        pausa(2000);    // Pausa para que el jugador lea el mensaje
    }

    public void jugar() {
        System.out.println("\n Juego BACKGAMMON");
        System.out.println("IA: ⚪ BLANCAS | Humano: ⚫ NEGRAS");
        System.out.println("Casillas del 0 al 23");
        pausa(2000);    // Tiempo para leer las instrucciones

        while (!estado.juegoTerminado()) {        // Mientras nadie haya sacado todas sus fichas
            mostrarTablero();                       // Dibuja el estado actual
            pausa(1000);                             // Tiempo para analizar el tablero

            if (estado.isTurnoIA()) {                // Si es turno de la IA
                turnoIA();                              // Ejecuta el turno de la IA
            } else {                                  // Si es turno del humano
                turnoHumano();                          // Ejecuta el turno del humano
            }

            estado.setTurnoIA(!estado.isTurnoIA());  // Cambia el turno al otro jugador
            pausa(1500);                              // Pausa entre turnos
        }

        mostrarTablero();                           // Muestra tablero final
        System.out.println("\nJuego terminado");
        System.out.println(" Ganador: " + estado.getGanador()); // Anuncia quién ganó
    }

    private void turnoIA() {
        System.out.println("\n Turno de la IA (⚪ BLANCAS)");
        pausa(1000);    // Tiempo para anunciar el turno

        int dado = lanzarDado();          // Genera número aleatorio 1-6
        estado.setDadoAct(dado);          // Guarda el valor del dado en el estado
        System.out.println("Dado: " + dado);
        pausa(1000);    // Tiempo para mostrar el dado

        System.out.print("Pensando");
        for (int i = 0; i < 3; i++) {     // Animación de "pensando"
            pausa(500);
            System.out.print(".");
        }
        System.out.println();

        Jugada mejorMovimiento = ia.buscarMejorMovimiento(estado);  // IA calcula mejor jugada con MIN-MAX

        if (mejorMovimiento == null) {     // Si no hay movimientos válidos
            System.out.println("La IA no tiene movimientos disponibles. Pierde turno.");
            pausa(2000);
            return;
        }

        System.out.println("Juega: " + mejorMovimiento);  // Muestra la jugada elegida
        pausa(1500);    // Tiempo para leer la jugada

        ReglasBackgammon.aplicarMovimiento(estado, mejorMovimiento);  // Ejecuta el movimiento en el tablero

        if (mejorMovimiento.isEsCaptura()) {    // Si la jugada capturó una ficha
            System.out.println("Captura");        // Avisa al jugador
            pausa(1000);
        }

        System.out.println("\nEl turno de la IA fue completado");
        pausa(1500);    // Pausa antes de cambiar de turno
    }

    private void turnoHumano() {
        System.out.println("\n turno del jugador humano (⚫ NEGRAS)");
        pausa(1000);

        int dado = lanzarDado();          // Genera número aleatorio 1-6
        estado.setDadoAct(dado);          // Guarda el valor del dado
        System.out.println(" Dado: " + dado);
        pausa(1000);

        List<Jugada> movimientos = MovimientosValidos.generarMovimientos(estado);  // Calcula todos los movimientos posibles

        if (movimientos.isEmpty()) {       // Si no hay movimientos válidos
            System.out.println("No hay movimientos disponibles. Pierdes turno.");
            pausa(2000);
            return;
        }

        System.out.println("\n Opciones disponibles:");
        for (int i = 0; i < movimientos.size(); i++) {    // Muestra todas las opciones numeradas
            System.out.println("   " + (i+1) + ". " + movimientos.get(i));
            pausa(300);    // Pequeña pausa entre opciones para facilitar lectura
        }

        int opcion = -1;
        while (opcion < 1 || opcion > movimientos.size()) {    // Bucle hasta elegir opción válida
            System.out.print("\n Elige (1-" + movimientos.size() + "): ");
            try {
                opcion = Integer.parseInt(scanner.nextLine());  // Lee la opción del usuario
            } catch (Exception e) {                              // Si no es número válido
                System.out.println("El numero es invalido");
            }
        }

        Jugada movimiento = movimientos.get(opcion - 1);  // Obtiene la jugada seleccionada
        System.out.println("Ejecutando: " + movimiento);
        pausa(1500);

        ReglasBackgammon.aplicarMovimiento(estado, movimiento);  // Ejecuta el movimiento

        if (movimiento.isEsCaptura()) {    // Si hubo captura
            System.out.println("Capturado");
            pausa(1000);
        }

        System.out.println("\n Turno completado");
        pausa(1500);    // Pausa antes de cambiar turno
    }

    private int lanzarDado() {
        return (int)(Math.random() * 6) + 1;  // Genera número aleatorio entre 1 y 6
    }

    private void pausa(int milisegundos) {
        try {
            Thread.sleep(milisegundos);    // Detiene la ejecución el tiempo indicado
        } catch (InterruptedException e) {
            // Ignorar excepción si se interrumpe la pausa
        }
    }

    private void mostrarTablero() {
        int[] t = estado.getTablero();    // Obtiene el array con las fichas por casilla

        System.out.println("\n" + "═".repeat(90));
        System.out.println("                              Tablero");
        System.out.println("═".repeat(90));

        // Muestra fichas fuera y capturadas de ambos jugadores
        System.out.printf("   FUERA: ⚪ %2d   |   ⚫ %2d      CAPTURADAS: ⚪ %2d  ⚫ %2d%n",
                estado.getFichasTIA(), estado.getFichasTHum(),
                estado.getFichasCapHum(), estado.getFichasCapIA());
        System.out.println("─".repeat(90));

        // Fila superior (12-23) - Números de casilla
        System.out.println("\n        12        13       14       15       16        17       │      18        19       20       21       22       23");
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
        System.out.println("    ← SALIDA IA");    // Indica dirección de salida para IA

        // Barra - zona central donde van las fichas capturadas
        System.out.println("\n    ───────────────────────────────────────────┼───────────────────────────────────────────");
        System.out.println("                    B A R R A                    │                 B A R R A");
        System.out.println("    ───────────────────────────────────────────┼───────────────────────────────────────────");

        // Fila inferior (11-0) - Números de casilla
        System.out.println("\n        11        10       9        8        7         6        │       5         4        3        2        1        0");
        System.out.print("     ");

        // Casillas 11-6 (recorre en orden descendente porque es la fila inferior)
        for (int i = 11; i >= 6; i--) {
            System.out.print("  " + formatearCasilla(t[i]) + "   ");
        }
        System.out.print("   │   ");

        // Casillas 5-0 (recorre en orden descendente)
        for (int i = 5; i >= 0; i--) {
            System.out.print("  " + formatearCasilla(t[i]) + "   ");
        }
        System.out.println("    ← Salida del jugador Humano");

        System.out.println("─".repeat(90));
        System.out.println("   " + estado);    // Muestra resumen del estado
        System.out.println("═".repeat(90));
    }

    private String formatearCasilla(int valor) {
        if (valor > 0) {    // Fichas blancas (IA)
            if (valor < 10) {
                return "⚪ " + valor + " ";    // Espacio extra para números de 1 dígito (alineación)
            } else {
                return "⚪" + valor + " ";      // Sin espacio para números de 2 dígitos
            }
        } else if (valor < 0) {    // Fichas negras (Humano)
            int num = -valor;                   // Convierte a positivo para mostrar
            if (num < 10) {
                return "⚫ " + num + " ";
            } else {
                return "⚫" + num + " ";
            }
        } else {
            return " ·  ";    // Casilla vacía - puntos para mantener alineación
        }
    }

    public static void main(String[] args) {
        JuegoBackgammon juego = new JuegoBackgammon();    // Crea el juego
        juego.jugar();                                     // Inicia la partida
    }
}