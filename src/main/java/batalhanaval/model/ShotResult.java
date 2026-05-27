package batalhanaval.model;

/**
 * Resultado de um tiro no tabuleiro inimigo.
 */
public enum ShotResult {
    /** Nenhum navio na célula. */
    MISS,
    /** Navio atingido, mas ainda não afundou. */
    HIT,
    /** Navio atingido e afundado. O campo {@link #sunkShipName} contém o nome. */
    SUNK;

    /** Nome do navio afundado (preenchido somente quando {@code this == SUNK}). */
    public String sunkShipName;

    /** Fábrica conveniente para resultados SUNK. */
    public static ShotResult sunk(String shipName) {
        ShotResult r = SUNK;
        r.sunkShipName = shipName;
        return r;
    }
}
