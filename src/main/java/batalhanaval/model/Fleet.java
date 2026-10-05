package batalhanaval.model;

import java.util.Random;

/**
 * Gerencia o posicionamento e o estado de uma frota sobre um {@link Board}.
 *
 * <p>A {@code Fleet} é responsável por:</p>
 * <ul>
 *   <li>Validar e realizar o posicionamento de navios (manual ou aleatório).</li>
 *   <li>Receber tiros e retornar o resultado via {@link ShotResult}.</li>
 *   <li>Consultar o estado geral da frota (navios vivos, todos afundados).</li>
 * </ul>
 */
public class Fleet {

    private final Board   board;
    private final Ship[]  ships;
    /** Índice do navio que ocupa cada célula, -1 se vazia. */
    private final int[][] shipIndex;

    public Fleet(Board board, Ship[] ships) {
        this.board     = board;
        this.ships     = ships;
        this.shipIndex = new int[Board.SIZE][Board.SIZE];
        for (int r = 0; r < Board.SIZE; r++)
            for (int c = 0; c < Board.SIZE; c++)
                shipIndex[r][c] = -1;
    }

    // ------------------------------------------------------------------
    //  Posicionamento
    // ------------------------------------------------------------------

    /**
     * Verifica se o navio cabe na posição sem sair da grade ou colidir.
     *
     * @param row        linha inicial (0-based)
     * @param col        coluna inicial (0-based)
     * @param size       tamanho do navio
     * @param horizontal {@code true} → cresce para a direita; {@code false} → para baixo
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
     * Posiciona o navio de índice {@code shipId} no tabuleiro.
     * Pré-condição: {@link #canPlace} deve retornar {@code true}.
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

    /** Posiciona toda a frota em posições aleatórias válidas. */
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

    // ------------------------------------------------------------------
    //  Combate
    // ------------------------------------------------------------------

    /**
     * Aplica um tiro na célula (row, col).
     *
     * @return {@link ShotResult} indicando água, acerto ou afundamento
     * @throws IllegalArgumentException se a célula estiver fora do tabuleiro
     * @throws IllegalStateException    se a célula já tiver sido atingida
     *                                  (sem esta checagem, um acerto repetido viraria "água")
     */
    public ShotResult receiveShot(int row, int col) {
        if (!board.inBounds(row, col)) {
            throw new IllegalArgumentException("Tiro fora do tabuleiro: " + row + "," + col);
        }
        Board.Cell cell = board.get(row, col);
        if (cell == Board.Cell.HIT || cell == Board.Cell.MISS) {
            throw new IllegalStateException("Célula já atingida: " + row + "," + col);
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

    // ------------------------------------------------------------------
    //  Consultas
    // ------------------------------------------------------------------

    /** Retorna {@code true} se todos os navios da frota foram afundados. */
    public boolean allSunk() {
        for (Ship s : ships) if (!s.isSunk()) return false;
        return true;
    }

    /**
     * Nome do navio que ocupa a célula, ou {@code null} se não houver navio.
     * Usado para anunciar qual navio foi afundado.
     */
    public String shipNameAt(int row, int col) {
        int id = shipIndex[row][col];
        return id < 0 ? null : ships[id].getName();
    }

    /** Conta quantos navios ainda não foram afundados. */
    public int shipsAlive() {
        int count = 0;
        for (Ship s : ships) if (!s.isSunk()) count++;
        return count;
    }
}
