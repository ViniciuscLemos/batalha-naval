package battleship.model;

/**
 * 10x10 Battleship board.
 *
 * Each cell is in one of four states: {@code EMPTY}, {@code SHIP},
 * {@code HIT} or {@code MISS}. The board doesn't know any game rules,
 * it only stores the state.
 */
public class Board {

    /** Board size (rows = columns = SIZE). */
    public static final int SIZE = 10;

    /** Possible state of each cell. */
    public enum Cell {
        EMPTY,  // empty / not revealed
        SHIP,   // ship (only shown on the player's own board)
        HIT,
        MISS
    }

    private final Cell[][] grid;

    public Board() {
        grid = new Cell[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                grid[r][c] = Cell.EMPTY;
    }

    /**
     * Returns the cell state.
     *
     * @throws IndexOutOfBoundsException if row/col are out of bounds
     */
    public Cell get(int row, int col) {
        return grid[row][col];
    }

    /** Sets the cell state. */
    public void set(int row, int col, Cell value) {
        grid[row][col] = value;
    }

    /** Returns {@code true} if row and col are inside the board. */
    public boolean inBounds(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }
}
