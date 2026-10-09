package battleship.ui;

import battleship.model.Board;

/**
 * Draws Battleship boards in the terminal.
 *
 * This class only has static methods and can't be instantiated.
 */
public final class BoardPrinter {

    private static final String HEADER = "  A B C D E F G H I J";
    /** Visible width of a board row ("10" + 10 cells), without the color codes. */
    private static final int ROW_WIDTH = 22;

    private static final String RESET = "[0m";
    private static final String DIM   = "[90m";
    private static final String CYAN  = "[36m";
    private static final String RED   = "[1;31m";
    private static final String BLUE  = "[34m";

    /**
     * Colors only when it's a real terminal, so redirecting the output to a file
     * doesn't fill it with escape codes. NO_COLOR turns them off and FORCE_COLOR on.
     */
    private static final boolean COLORS = System.getenv("NO_COLOR") == null
            && (System.getenv("FORCE_COLOR") != null || System.console() != null);

    private BoardPrinter() {}

    /**
     * Shows two boards side by side:
     * the player's board (with ships) on the left, the shots at the enemy on the right.
     *
     * @param ownBoard     board with the player's ships
     * @param shotsBoard   board with the player's shots at the enemy
     */
    public static void printSideBySide(Board ownBoard, Board shotsBoard) {
        System.out.printf("%-26s  |  %s%n", "YOUR BOARD", "SHOTS AT THE ENEMY");
        System.out.printf("%-26s  |  %s%n", HEADER, HEADER);
        for (int r = 0; r < Board.SIZE; r++) {
            // %-26s would count the color codes as characters, so the padding is done by hand
            String left  = buildRow(r, ownBoard,   true) + " ".repeat(26 - ROW_WIDTH);
            String right = buildRow(r, shotsBoard, false);
            System.out.println(left + "  |  " + right);
        }
        System.out.println("Legend: S=ship  X=hit  o=miss  .=empty");
    }

    /**
     * Shows a single board with a title.
     *
     * @param title      title shown above the board
     * @param board      board to draw
     * @param showShips  if {@code true}, shows 'S' on cells with a ship
     */
    public static void printSingle(String title, Board board, boolean showShips) {
        System.out.println(title);
        System.out.println(HEADER);
        for (int r = 0; r < Board.SIZE; r++) {
            System.out.println(buildRow(r, board, showShips));
        }
    }

    private static String buildRow(int row, Board board, boolean showShips) {
        StringBuilder sb = new StringBuilder(String.format("%2d", row + 1));
        for (int c = 0; c < Board.SIZE; c++) {
            Board.Cell cell = board.get(row, c);
            char ch = toChar(cell, showShips);
            sb.append(' ');
            if (COLORS) sb.append(color(ch)).append(ch).append(RESET);
            else sb.append(ch);
        }
        return sb.toString();
    }

    private static String color(char ch) {
        return switch (ch) {
            case 'S' -> CYAN;
            case 'X' -> RED;
            case 'o' -> BLUE;
            default  -> DIM;
        };
    }

    private static char toChar(Board.Cell cell, boolean showShips) {
        return switch (cell) {
            case SHIP  -> showShips ? 'S' : '.';
            case HIT   -> 'X';
            case MISS  -> 'o';
            default    -> '.';
        };
    }
}
