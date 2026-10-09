package battleship.cpu;

/**
 * Contract for a CPU AI strategy.
 *
 * Implementations are interchangeable, so the strategy can be swapped
 * without touching the rest of the code (Open/Closed).
 */
public interface CpuStrategy {

    /**
     * Picks the next cell to shoot.
     *
     * @return {@code int[]{row, col}} with 0-based indexes
     */
    int[] chooseTarget();

    /**
     * Gets the result of the last shot to guide the next pick.
     *
     * @param row    row that was hit
     * @param col    column that was hit
     * @param result result of the shot
     */
    void registerResult(int row, int col, battleship.model.ShotResult result);
}
