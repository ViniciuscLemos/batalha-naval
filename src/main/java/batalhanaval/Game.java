package batalhanaval;

import batalhanaval.cpu.CpuStrategy;
import batalhanaval.engine.CoordParser;
import batalhanaval.engine.GameLog;
import batalhanaval.model.*;
import batalhanaval.ui.BoardPrinter;

import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

/** Controla a partida. A IA fica em CpuStrategy e a impressão em BoardPrinter. */
public class Game {

    private static final String[] SHIP_NAMES = {
            "Porta-aviões", "Encouraçado", "Cruzador", "Submarino", "Destroyer"
    };
    private static final int[] SHIP_SIZES = {5, 4, 3, 3, 2};

    private final Scanner     sc;
    private final Random      rng;
    private final CpuStrategy cpuStrategy;
    private final GameLog     log;

    private final Board playerBoard;
    private final Board playerShotsBoard;  // onde o jogador já atirou
    private final Board cpuBoard;

    private final Fleet playerFleet;
    private final Fleet cpuFleet;

    private int playerShots, playerHits, cpuShots, cpuHits;

    public Game(Scanner sc, Random rng, CpuStrategy cpuStrategy) {
        this.sc          = sc;
        this.rng         = rng;
        this.cpuStrategy = cpuStrategy;
        this.log         = new GameLog();

        playerBoard      = new Board();
        playerShotsBoard = new Board();
        cpuBoard         = new Board();

        playerFleet = new Fleet(playerBoard, buildShips());
        cpuFleet    = new Fleet(cpuBoard,    buildShips());
    }

    public void run() {
        System.out.println("=== BATALHA NAVAL ===");
        setupPlayerFleet();
        cpuFleet.placeAllRandom(rng);
        playLoop();
        printStats();
        askShowLog();
    }

    private void setupPlayerFleet() {
        System.out.println("\nPosicionamento da frota. Coordenadas: A–J e 1–10 (ex: A1, J10).");
        System.out.print("Deseja posicionar manualmente? (s/N): ");
        String answer = sc.nextLine().trim().toLowerCase(Locale.ROOT);

        if (answer.equals("s") || answer.equals("sim")) {
            placeManually();
        } else {
            playerFleet.placeAllRandom(rng);
            System.out.println("Frota posicionada automaticamente.");
        }
    }

    private void placeManually() {
        for (int id = 0; id < SHIP_NAMES.length; id++) {
            boolean placed = false;
            while (!placed) {
                System.out.println();
                BoardPrinter.printSingle("SEU TABULEIRO", playerBoard, true);
                System.out.printf("Posicionando: %s (tamanho %d)%n", SHIP_NAMES[id], SHIP_SIZES[id]);

                System.out.print("Coordenada inicial (ex A1): ");
                int[] rc = CoordParser.parse(sc.nextLine());
                if (rc == null) { System.out.println("Coordenada inválida."); continue; }

                System.out.print("Direção (H=horizontal / V=vertical): ");
                String dir = sc.nextLine().trim().toUpperCase(Locale.ROOT);
                if (!dir.equals("H") && !dir.equals("V")) { System.out.println("Direção inválida."); continue; }

                boolean horiz = dir.equals("H");
                if (!playerFleet.canPlace(rc[0], rc[1], SHIP_SIZES[id], horiz)) {
                    System.out.println("Não cabe ou colide. Tente novamente.");
                    continue;
                }
                playerFleet.place(id, rc[0], rc[1], horiz);
                log.add("Jogador posicionou " + SHIP_NAMES[id] + " em " + CoordParser.format(rc[0], rc[1]));
                placed = true;
            }
        }
    }

    private void playLoop() {
        boolean playerTurn = true;

        while (true) {
            printStatus();

            if (cpuFleet.allSunk()) {
                System.out.println("\n*** VITÓRIA! Você afundou toda a frota inimiga. ***");
                log.add("Fim: vitória do jogador");
                break;
            }
            if (playerFleet.allSunk()) {
                System.out.println("\n*** DERROTA. Sua frota foi afundada. ***");
                log.add("Fim: vitória da CPU");
                break;
            }

            if (playerTurn) {
                playerTurn = !doPlayerTurn(); // turno consumido → troca
            } else {
                doCpuTurn();
                playerTurn = true;
            }
        }
    }

    private void printStatus() {
        System.out.println();
        BoardPrinter.printSideBySide(playerBoard, playerShotsBoard);
        System.out.printf("Navios restantes: você %d | CPU %d%n",
                playerFleet.shipsAlive(), cpuFleet.shipsAlive());
    }

    /**
     * Processa o turno do jogador.
     *
     * @return {@code true} se um tiro foi disparado (turno consumido),
     *         {@code false} se o jogador escolheu outra ação (log, tabuleiro)
     */
    private boolean doPlayerTurn() {
        System.out.println("\n--- Seu turno ---");
        System.out.println("Digite a coordenada pra atirar (ex B7), ou: 2) Ver log   3) Ver seu tabuleiro");
        System.out.print("> ");
        String opt = sc.nextLine().trim();

        if ("2".equals(opt)) { log.printTail(10);                                              return false; }
        if ("3".equals(opt)) { BoardPrinter.printSingle("SEU TABULEIRO", playerBoard, true);   return false; }

        // dá pra atirar direto digitando a coordenada; o "1" do menu antigo continua funcionando
        int[] rc = CoordParser.parse(opt);
        if (rc == null && "1".equals(opt)) {
            System.out.print("Coordenada para atirar (ex B7): ");
            rc = CoordParser.parse(sc.nextLine());
        }
        if (rc == null) { System.out.println("Coordenada inválida."); return false; }

        if (playerShotsBoard.get(rc[0], rc[1]) != Board.Cell.EMPTY) {
            System.out.println("Você já atirou nessa posição."); return false;
        }

        ShotResult result = cpuFleet.receiveShot(rc[0], rc[1]);
        playerShotsBoard.set(rc[0], rc[1],
                result == ShotResult.MISS ? Board.Cell.MISS : Board.Cell.HIT);
        playerShots++;
        if (result != ShotResult.MISS) playerHits++;

        String msg = formatResult(result, cpuFleet.shipNameAt(rc[0], rc[1]));
        System.out.println(msg + " em " + CoordParser.format(rc[0], rc[1]));
        log.add("Jogador: " + msg + " em " + CoordParser.format(rc[0], rc[1]));
        return true;
    }

    private void doCpuTurn() {
        System.out.println("\n--- Turno da CPU ---");
        int[] target = cpuStrategy.chooseTarget();
        if (target == null) return;

        ShotResult result = playerFleet.receiveShot(target[0], target[1]);
        cpuStrategy.registerResult(target[0], target[1], result);
        cpuShots++;
        if (result != ShotResult.MISS) cpuHits++;

        String msg = formatResult(result, playerFleet.shipNameAt(target[0], target[1]));
        System.out.println("CPU: " + msg + " em " + CoordParser.format(target[0], target[1]));
        log.add("CPU: " + msg + " em " + CoordParser.format(target[0], target[1]));
    }

    private void askShowLog() {
        System.out.print("\nMostrar log completo? (s/N): ");
        String s = sc.nextLine().trim().toLowerCase(Locale.ROOT);
        if (s.equals("s") || s.equals("sim")) log.printAll();
    }

    private static String formatResult(ShotResult r, String shipName) {
        return switch (r) {
            case MISS -> "ÁGUA";
            case HIT  -> "ACERTO";
            case SUNK -> "AFUNDOU: " + shipName;
        };
    }

    private void printStats() {
        System.out.println("\n--- Estatísticas da partida ---");
        System.out.printf("Você: %d tiros, %d acertos (%s)%n", playerShots, playerHits, percent(playerHits, playerShots));
        System.out.printf("CPU:  %d tiros, %d acertos (%s)%n", cpuShots, cpuHits, percent(cpuHits, cpuShots));
    }

    private static String percent(int part, int total) {
        return total == 0 ? "-" : String.format("%.0f%%", 100.0 * part / total);
    }

    private static Ship[] buildShips() {
        Ship[] arr = new Ship[SHIP_NAMES.length];
        for (int i = 0; i < arr.length; i++) arr[i] = new Ship(SHIP_NAMES[i], SHIP_SIZES[i]);
        return arr;
    }
}
