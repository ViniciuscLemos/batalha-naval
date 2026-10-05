package batalhanaval.model;

/**
 * Tabuleiro 10×10 do Batalha Naval.
 *
 * Cada célula pode estar em um de quatro estados: {@code EMPTY},
 * {@code SHIP}, {@code HIT} ou {@code MISS}. O tabuleiro não conhece
 * nenhuma regra de jogo, apenas armazena e expõe estado.
 */
public class Board {

    /** Dimensão do tabuleiro (linhas = colunas = SIZE). */
    public static final int SIZE = 10;

    /** Estado possível de cada célula. */
    public enum Cell {
        EMPTY,  // vazio / não revelado
        SHIP,   // navio (visível apenas no tabuleiro do próprio jogador)
        HIT,    // acerto
        MISS    // água
    }

    private final Cell[][] grid;

    public Board() {
        grid = new Cell[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                grid[r][c] = Cell.EMPTY;
    }

    /**
     * Retorna o estado da célula.
     *
     * @throws IndexOutOfBoundsException se row/col estiverem fora dos limites
     */
    public Cell get(int row, int col) {
        return grid[row][col];
    }

    /** Define o estado da célula. */
    public void set(int row, int col, Cell value) {
        grid[row][col] = value;
    }

    /** Retorna {@code true} se row e col estiverem dentro dos limites. */
    public boolean inBounds(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }
}
