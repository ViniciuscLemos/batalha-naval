package batalhanaval.engine;

import batalhanaval.model.Board;

import java.util.Locale;

/**
 * Converte entre notação do usuário ("A1"–"J10") e índices internos [row, col].
 *
 * A convenção adotada é: letras A–J representam <em>colunas</em> (eixo x),
 * números 1–10 representam <em>linhas</em> (eixo y). Internamente, row e col
 * são índices 0-based.
 *
 * Esta classe não pode ser instanciada, todos os métodos são estáticos.
 */
public final class CoordParser {

    private CoordParser() {}

    /**
     * Parseia a coordenada digitada pelo usuário.
     *
     * @param input string no formato "A1"–"J10" (case-insensitive)
     * @return {@code int[]{row, col}} com índices 0-based,
     *         ou {@code null} se a entrada for inválida
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
     * Converte índices internos de volta para notação legível pelo usuário.
     *
     * @param row linha 0-based
     * @param col coluna 0-based
     * @return string no formato "A1"–"J10"
     */
    public static String format(int row, int col) {
        return "" + (char) ('A' + col) + (row + 1);
    }
}
