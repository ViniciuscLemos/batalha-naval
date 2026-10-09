package battleship.cpu;

import battleship.model.Board;
import battleship.model.ShotResult;

import java.util.ArrayDeque;
import java.util.Random;

/**
 * Hunt-and-Target strategy.
 *
 * How it plays:
 * <ol>
 *   <li><b>Hunt:</b> shoots at random, preferring cells whose index sum
 *       is even, which lowers the average number of shots needed.</li>
 *   <li><b>Target:</b> after hitting a ship, queues the four neighboring
 *       cells to try next.</li>
 *   <li>After sinking a ship, drops the target queue (60% of the time)
 *       and goes back to hunting, so it doesn't waste shots around the wreck.</li>
 * </ol>
 */
public class HuntTargetStrategy implements CpuStrategy {

    private final Random             rng;
    private final boolean[][]        tried;
    private final ArrayDeque<int[]>  targets;

    public HuntTargetStrategy(Random rng) {
        this.rng     = rng;
        this.tried   = new boolean[Board.SIZE][Board.SIZE];
        this.targets = new ArrayDeque<>();
    }

    @Override
    public int[] chooseTarget() {
        // Target phase: use up the pending targets from earlier hits
        while (!targets.isEmpty()) {
            int[] t = targets.removeFirst();
            int r = t[0], c = t[1];
            if (Board.SIZE > r && r >= 0 && Board.SIZE > c && c >= 0 && !tried[r][c]) {
                return t;
            }
        }

        // Hunt phase: random, preferring the checkerboard cells
        for (int attempt = 0; attempt < 5_000; attempt++) {
            int r = rng.nextInt(Board.SIZE);
            int c = rng.nextInt(Board.SIZE);
            if (!tried[r][c] && ((r + c) % 2 == 0 || rng.nextInt(100) < 25)) {
                return new int[]{r, c};
            }
        }

        // Fallback: first cell not tried yet
        for (int r = 0; r < Board.SIZE; r++)
            for (int c = 0; c < Board.SIZE; c++)
                if (!tried[r][c]) return new int[]{r, c};

        return null; // all 100 cells were tried, the game is about to end
    }

    @Override
    public void registerResult(int row, int col, ShotResult result) {
        tried[row][col] = true;

        if (result == ShotResult.HIT) {
            enqueueNeighbors(row, col);
        } else if (result == ShotResult.SUNK) {
            if (rng.nextInt(100) < 60) {
                targets.clear(); // drop the wreck, go back to hunting
            } else {
                enqueueNeighbors(row, col);
            }
        }
    }

    private void enqueueNeighbors(int row, int col) {
        targets.addLast(new int[]{row + 1, col});
        targets.addLast(new int[]{row - 1, col});
        targets.addLast(new int[]{row, col + 1});
        targets.addLast(new int[]{row, col - 1});
    }
}
