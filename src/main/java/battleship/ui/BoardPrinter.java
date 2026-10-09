package battleship.ui;

import battleship.model.Board;

/**
 * Draws Battleship boards in the terminal.
 *
 * This class only has static methods and can't be instantiated.
 */
public final class BoardPrinter {

    private static final String HEADER = "  A B C D E F G H I J";

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
            String left  = buildRow(r, ownBoard,   true);
            String right = buildRow(r, shotsBoard, false);
            System.out.printf("%-26s  |  %s%n", left, right);
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
            sb.append(' ').append(toChar(board.get(row, c), showShips));
        }
        return sb.toString();
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
