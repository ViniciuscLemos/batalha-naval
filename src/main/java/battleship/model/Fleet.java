package battleship.model;

import java.util.Random;

/**
 * Handles placing and tracking a fleet on a {@link Board}.
 *
 * {@code Fleet} is responsible for:
 * <ul>
 *   <li>Checking and placing ships (by hand or at random).</li>
 *   <li>Taking shots and returning the result as a {@link ShotResult}.</li>
 *   <li>Telling the overall fleet state (ships alive, all sunk).</li>
 * </ul>
 */
public class Fleet {

    private final Board   board;
    private final Ship[]  ships;
    /** Index of the ship on each cell, -1 if empty. */
    private final int[][] shipIndex;

    public Fleet(Board board, Ship[] ships) {
        this.board     = board;
        this.ships     = ships;
        this.shipIndex = new int[Board.SIZE][Board.SIZE];
        for (int r = 0; r < Board.SIZE; r++)
            for (int c = 0; c < Board.SIZE; c++)
                shipIndex[r][c] = -1;
    }

    /**
     * Checks if the ship fits at the position without leaving the grid or overlapping.
     *
     * @param row        starting row (0-based)
     * @param col        starting column (0-based)
     * @param size       ship size
     * @param horizontal {@code true} grows to the right; {@code false} grows down
     */
    public boolean canPlace(int row, int col, int size, boolean horizontal) {
        for (int i = 0; i < size; i++) {
            int r = horizontal ? row     : row + i;
            int c = horizontal ? col + i : col;
            if (!board.inBounds(r, c)) return false;
            if (board.get(r, c) != Board.Cell.EMPTY) return false;
        }
        return true;
    }

    /**
     * Places the ship with index {@code shipId} on the board.
     * Precondition: {@link #canPlace} must return {@code true}.
     */
    public void place(int shipId, int row, int col, boolean horizontal) {
        int size = ships[shipId].getSize();
        for (int i = 0; i < size; i++) {
            int r = horizontal ? row     : row + i;
            int c = horizontal ? col + i : col;
            board.set(r, c, Board.Cell.SHIP);
            shipIndex[r][c] = shipId;
        }
    }

    /** Places the whole fleet at random valid positions. */
    public void placeAllRandom(Random rng) {
        for (int id = 0; id < ships.length; id++) {
            boolean placed = false;
            while (!placed) {
                boolean horiz = rng.nextBoolean();
                int row = rng.nextInt(Board.SIZE);
                int col = rng.nextInt(Board.SIZE);
                if (canPlace(row, col, ships[id].getSize(), horiz)) {
                    place(id, row, col, horiz);
                    placed = true;
                }
            }
        }
    }

    /**
     * Fires a shot at cell (row, col).
     *
     * @return {@link ShotResult} saying miss, hit or sunk
     * @throws IllegalArgumentException if the cell is outside the board
     * @throws IllegalStateException    if the cell was already shot
     *                                  (without this check, a repeated hit would turn into a "miss")
     */
    public ShotResult receiveShot(int row, int col) {
        if (!board.inBounds(row, col)) {
            throw new IllegalArgumentException("Shot outside the board: " + row + "," + col);
        }
        Board.Cell cell = board.get(row, col);
        if (cell == Board.Cell.HIT || cell == Board.Cell.MISS) {
            throw new IllegalStateException("Cell already shot: " + row + "," + col);
        }

        if (cell == Board.Cell.SHIP) {
            board.set(row, col, Board.Cell.HIT);
            Ship ship = ships[shipIndex[row][col]];
            ship.hit();
            return ship.isSunk() ? ShotResult.SUNK : ShotResult.HIT;
        }
        board.set(row, col, Board.Cell.MISS);
        return ShotResult.MISS;
    }

    /** Returns {@code true} if every ship in the fleet was sunk. */
    public boolean allSunk() {
        for (Ship s : ships) if (!s.isSunk()) return false;
        return true;
    }

    /**
     * Name of the ship on the cell, or {@code null} if there's no ship.
     * Used to announce which ship was sunk.
     */
    public String shipNameAt(int row, int col) {
        int id = shipIndex[row][col];
        return id < 0 ? null : ships[id].getName();
    }

    /** Counts how many ships haven't been sunk yet. */
    public int shipsAlive() {
        int count = 0;
        for (Ship s : ships) if (!s.isSunk()) count++;
        return count;
    }
}
