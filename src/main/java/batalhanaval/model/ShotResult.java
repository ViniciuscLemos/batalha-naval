package batalhanaval.model;

/**
 * Resultado de um tiro no tabuleiro inimigo.
 *
 * <p>Constantes de enum são únicas na JVM inteira, por isso este enum não
 * guarda estado (como o nome do navio afundado): isso seria compartilhado
 * entre todos os tiros. O nome é obtido com {@link Fleet#shipNameAt(int, int)}.</p>
 */
public enum ShotResult {
    /** Nenhum navio na célula. */
    MISS,
    /** Navio atingido, mas ainda não afundou. */
    HIT,
    /** Navio atingido e afundado. */
    SUNK
}
