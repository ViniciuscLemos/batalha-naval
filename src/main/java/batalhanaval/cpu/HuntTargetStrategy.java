package batalhanaval.cpu;

import batalhanaval.model.Board;
import batalhanaval.model.ShotResult;

import java.util.ArrayDeque;
import java.util.Random;

/**
 * Estratégia Hunt-and-Target (Caça e Destruição).
 *
 * Comportamento:
 * <ol>
 *   <li><b>Hunt (caça):</b> atira aleatoriamente, com preferência por células
 *       cuja soma de índices seja par, isso reduz a média de tiros necessários.</li>
 *   <li><b>Target (destruição):</b> ao acertar um navio, enfileira as quatro
 *       células vizinhas para tentar a seguir.</li>
 *   <li>Ao afundar um navio, descarta a fila de alvos (com probabilidade 60%)
 *       e volta ao modo caça, evitando desperdiçar tiros em torno do destroço.</li>
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
        // Fase Target: esgota alvos pendentes de acertos anteriores
        while (!targets.isEmpty()) {
            int[] t = targets.removeFirst();
            int r = t[0], c = t[1];
            if (Board.SIZE > r && r >= 0 && Board.SIZE > c && c >= 0 && !tried[r][c]) {
                return t;
            }
        }

        // Fase Hunt: aleatório com preferência de paridade
        for (int attempt = 0; attempt < 5_000; attempt++) {
            int r = rng.nextInt(Board.SIZE);
            int c = rng.nextInt(Board.SIZE);
            if (!tried[r][c] && ((r + c) % 2 == 0 || rng.nextInt(100) < 25)) {
                return new int[]{r, c};
            }
        }

        // Fallback: primeira célula não tentada
        for (int r = 0; r < Board.SIZE; r++)
            for (int c = 0; c < Board.SIZE; c++)
                if (!tried[r][c]) return new int[]{r, c};

        return null; // todos os 100 quadrados foram tentados — fim de jogo iminente
    }

    @Override
    public void registerResult(int row, int col, ShotResult result) {
        tried[row][col] = true;

        if (result == ShotResult.HIT) {
            enqueueNeighbors(row, col);
        } else if (result == ShotResult.SUNK) {
            if (rng.nextInt(100) < 60) {
                targets.clear(); // descarta destroço, volta a caçar
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
