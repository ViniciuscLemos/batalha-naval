package battleship;

import battleship.cpu.CpuStrategy;
import battleship.engine.CoordParser;
import battleship.engine.GameLog;
import battleship.model.*;
import battleship.ui.BoardPrinter;

import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

/** Runs the match. The AI lives in CpuStrategy and the printing in BoardPrinter. */
public class Game {

    private static final String[] SHIP_NAMES = {
            "Carrier", "Battleship", "Cruiser", "Submarine", "Destroyer"
    };
    private static final int[] SHIP_SIZES = {5, 4, 3, 3, 2};

    private final Scanner     sc;
    private final Random      rng;
    private final CpuStrategy cpuStrategy;
    private final GameLog     log;

    private final Board playerBoard;
    private final Board playerShotsBoard;  // where the player already shot
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
        System.out.println("=== BATTLESHIP ===");
        setupPlayerFleet();
        cpuFleet.placeAllRandom(rng);
        playLoop();
        printStats();
        askShowLog();
    }

    private void setupPlayerFleet() {
        System.out.println("\nFleet placement. Coordinates: A-J and 1-10 (e.g. A1, J10).");
        System.out.print("Place the ships yourself? (y/N): ");
        String answer = sc.nextLine().trim().toLowerCase(Locale.ROOT);

        if (answer.equals("y") || answer.equals("yes")) {
            placeManually();
        } else {
            playerFleet.placeAllRandom(rng);
            System.out.println("Fleet placed automatically.");
        }
    }

    private void placeManually() {
        for (int id = 0; id < SHIP_NAMES.length; id++) {
            boolean placed = false;
            while (!placed) {
                System.out.println();
                BoardPrinter.printSingle("YOUR BOARD", playerBoard, true);
                System.out.printf("Placing: %s (size %d)%n", SHIP_NAMES[id], SHIP_SIZES[id]);

                System.out.print("Starting coordinate (e.g. A1): ");
                int[] rc = CoordParser.parse(sc.nextLine());
                if (rc == null) { System.out.println("Invalid coordinate."); continue; }

                System.out.print("Direction (H=horizontal / V=vertical): ");
                String dir = sc.nextLine().trim().toUpperCase(Locale.ROOT);
                if (!dir.equals("H") && !dir.equals("V")) { System.out.println("Invalid direction."); continue; }

                boolean horiz = dir.equals("H");
                if (!playerFleet.canPlace(rc[0], rc[1], SHIP_SIZES[id], horiz)) {
                    System.out.println("It doesn't fit or overlaps another ship. Try again.");
                    continue;
                }
                playerFleet.place(id, rc[0], rc[1], horiz);
                log.add("Player placed " + SHIP_NAMES[id] + " at " + CoordParser.format(rc[0], rc[1]));
                placed = true;
            }
        }
    }

    private void playLoop() {
        boolean playerTurn = true;
        // the boards are shown once per round, before the player's shot. An invalid
        // coordinate or opening the log doesn't print everything again
        boolean showBoards = true;

        while (true) {
            if (cpuFleet.allSunk()) {
                printStatus();
                System.out.println("\n*** VICTORY! You sank the whole enemy fleet. ***");
                log.add("End: player wins");
                break;
            }
            if (playerFleet.allSunk()) {
                printStatus();
                System.out.println("\n*** DEFEAT. Your fleet was sunk. ***");
                log.add("End: CPU wins");
                break;
            }

            if (playerTurn) {
                if (showBoards) printStatus();
                boolean used = doPlayerTurn();
                playerTurn = !used; // turn used up, switch
                showBoards = false;
            } else {
                doCpuTurn();
                playerTurn = true;
                showBoards = true;
            }
        }
    }

    private void printStatus() {
        System.out.println();
        BoardPrinter.printSideBySide(playerBoard, playerShotsBoard);
        System.out.printf("Ships left: you %d | CPU %d%n",
                playerFleet.shipsAlive(), cpuFleet.shipsAlive());
    }

    /**
     * Handles the player's turn.
     *
     * @return {@code true} if a shot was fired (turn used up),
     *         {@code false} if the player picked another action (log, board)
     */
    private boolean doPlayerTurn() {
        System.out.println("\n--- Your turn ---");
        System.out.println("Type the coordinate to shoot (e.g. B7), or: 2) Show log   3) Show your board");
        System.out.print("> ");
        String opt = sc.nextLine().trim();

        if ("2".equals(opt)) { log.printTail(10);                                              return false; }
        if ("3".equals(opt)) { BoardPrinter.printSingle("YOUR BOARD", playerBoard, true);      return false; }

        // you can shoot by typing the coordinate right away; the "1" from the old menu still works
        int[] rc = CoordParser.parse(opt);
        if (rc == null && "1".equals(opt)) {
            System.out.print("Coordinate to shoot (e.g. B7): ");
            rc = CoordParser.parse(sc.nextLine());
        }
        if (rc == null) { System.out.println("Invalid coordinate."); return false; }

        if (playerShotsBoard.get(rc[0], rc[1]) != Board.Cell.EMPTY) {
            System.out.println("You already shot there."); return false;
        }

        ShotResult result = cpuFleet.receiveShot(rc[0], rc[1]);
        playerShotsBoard.set(rc[0], rc[1],
                result == ShotResult.MISS ? Board.Cell.MISS : Board.Cell.HIT);
        playerShots++;
        if (result != ShotResult.MISS) playerHits++;

        String msg = formatResult(result, cpuFleet.shipNameAt(rc[0], rc[1]));
        System.out.println(msg + " at " + CoordParser.format(rc[0], rc[1]));
        log.add("Player: " + msg + " at " + CoordParser.format(rc[0], rc[1]));
        return true;
    }

    private void doCpuTurn() {
        System.out.println("\n--- CPU turn ---");
        int[] target = cpuStrategy.chooseTarget();
        if (target == null) return;

        ShotResult result = playerFleet.receiveShot(target[0], target[1]);
        cpuStrategy.registerResult(target[0], target[1], result);
        cpuShots++;
        if (result != ShotResult.MISS) cpuHits++;

        String msg = formatResult(result, playerFleet.shipNameAt(target[0], target[1]));
        System.out.println("CPU: " + msg + " at " + CoordParser.format(target[0], target[1]));
        log.add("CPU: " + msg + " at " + CoordParser.format(target[0], target[1]));
    }

    private void askShowLog() {
        System.out.print("\nShow the full log? (y/N): ");
        String s = sc.nextLine().trim().toLowerCase(Locale.ROOT);
        if (s.equals("y") || s.equals("yes")) log.printAll();
    }

    private static String formatResult(ShotResult r, String shipName) {
        return switch (r) {
            case MISS -> "MISS";
            case HIT  -> "HIT";
            case SUNK -> "SUNK: " + shipName;
        };
    }

    private void printStats() {
        System.out.println("\n--- Match stats ---");
        System.out.printf("You:  %d shots, %d hits (%s)%n", playerShots, playerHits, percent(playerHits, playerShots));
        System.out.printf("CPU:  %d shots, %d hits (%s)%n", cpuShots, cpuHits, percent(cpuHits, cpuShots));
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
