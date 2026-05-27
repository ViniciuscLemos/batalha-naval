package batalhanaval.model;

/**
 * Representa um navio da frota.
 *
 * <p>Encapsula nome, tamanho e pontos de vida (HP). O HP começa igual ao
 * tamanho e diminui a cada {@link #hit()} chamado. Quando chega a zero,
 * {@link #isSunk()} retorna {@code true}.</p>
 */
public class Ship {

    private final String name;
    private final int    size;
    private       int    hp;

    /**
     * Cria um navio com HP inicial igual ao tamanho.
     *
     * @param name nome amigável (ex: "Porta-aviões")
     * @param size número de células que o navio ocupa
     */
    public Ship(String name, int size) {
        if (size <= 0) throw new IllegalArgumentException("Tamanho deve ser positivo: " + size);
        this.name = name;
        this.size = size;
        this.hp   = size;
    }

    public String  getName()  { return name; }
    public int     getSize()  { return size; }
    public int     getHp()    { return hp;   }
    public boolean isSunk()   { return hp <= 0; }

    /** Aplica um acerto. HP não vai abaixo de zero. */
    public void hit() {
        if (hp > 0) hp--;
    }
}
