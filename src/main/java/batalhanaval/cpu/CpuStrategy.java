package batalhanaval.cpu;

/**
 * Define o contrato de uma estratégia de IA para a CPU.
 *
 * Implementações desta interface são intercambiáveis, permitindo
 * trocar de estratégia sem alterar o restante do código (Open/Closed).
 */
public interface CpuStrategy {

    /**
     * Escolhe a próxima célula para atirar.
     *
     * @return {@code int[]{row, col}} com índices 0-based
     */
    int[] chooseTarget();

    /**
     * Recebe o resultado do último tiro para guiar a próxima escolha.
     *
     * @param row    linha atingida
     * @param col    coluna atingida
     * @param result resultado do tiro
     */
    void registerResult(int row, int col, batalhanaval.model.ShotResult result);
}
