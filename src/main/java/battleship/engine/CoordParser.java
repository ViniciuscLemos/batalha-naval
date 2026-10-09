package battleship.engine;

import battleship.model.Board;

import java.util.Locale;

/**
 * Converts between the user's notation ("A1" to "J10") and internal [row, col] indexes.
 *
 * Letters A-J are <em>columns</em> (x axis) and numbers 1-10 are
 * <em>rows</em> (y axis). Internally, row and col are 0-based.
 *
 * This class can't be instantiated, all methods are static.
 */
public final class CoordParser {

    private CoordParser() {}

    /**
     * Parses the coordinate typed by the user.
     *
     * @param input string in the "A1" to "J10" format (case insensitive)
     * @return {@code int[]{row, col}} with 0-based indexes,
     *         or {@code null} if the input is invalid
     */
    public static int[] parse(String input) {
        if (input == null) return null;
        String s = input.trim().toUpperCase(Locale.ROOT).replace(" ", "");
        if (s.length() < 2 || s.length() > 3) return null;

        char colChar = s.charAt(0);
        if (colChar < 'A' || colChar > 'J') return null;
        int col = colChar - 'A';

        int row;
        try {
            row = Integer.parseInt(s.substring(1)) - 1;
        } catch (NumberFormatException e) {
            return null;
        }
        if (row < 0 || row >= Board.SIZE) return null;
        return new int[]{row, col};
    }

    /**
     * Converts internal indexes back to the user's notation.
     *
     * @param row 0-based row
     * @param col 0-based column
     * @return string in the "A1" to "J10" format
     */
    public static String format(int row, int col) {
        return "" + (char) ('A' + col) + (row + 1);
    }
}
