package batalhanaval.ui;

import batalhanaval.model.Board;

/**
 * Renderiza tabuleiros do Batalha Naval no terminal.
 *
 * <p>Esta classe possui apenas métodos estáticos — não pode ser instanciada.</p>
 */
public final class BoardPrinter {

    private static final String HEADER = "  A B C D E F G H I J";

    private BoardPrinter() {}

    /**
     * Exibe dois tabuleiros lado a lado:
     * à esquerda o tabuleiro do jogador (com navios), à direita os tiros no inimigo.
     *
     * @param ownBoard     tabuleiro com posição dos navios do jogador
     * @param shotsBoard   tabuleiro que registra os tiros do jogador no inimigo
     */
    public static void printSideBySide(Board ownBoard, Board shotsBoard) {
        System.out.printf("%-26s  |  %s%n", "SEU TABULEIRO", "TIROS NO INIMIGO");
        System.out.printf("%-26s  |  %s%n", HEADER, HEADER);
        for (int r = 0; r < Board.SIZE; r++) {
            String left  = buildRow(r, ownBoard,   true);
            String right = buildRow(r, shotsBoard, false);
            System.out.printf("%-26s  |  %s%n", left, right);
        }
        System.out.println("Legenda: S=navio  X=acerto  o=água  .=vazio");
    }

    /**
     * Exibe um único tabuleiro com um título.
     *
     * @param title      título exibido acima do tabuleiro
     * @param board      tabuleiro a renderizar
     * @param showShips  se {@code true}, exibe 'S' nas células com navio
     */
    public static void printSingle(String title, Board board, boolean showShips) {
        System.out.println(title);
        System.out.println(HEADER);
        for (int r = 0; r < Board.SIZE; r++) {
            System.out.println(buildRow(r, board, showShips));
        }
    }

    // ------------------------------------------------------------------
    //  Helpers privados
    // ------------------------------------------------------------------

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
