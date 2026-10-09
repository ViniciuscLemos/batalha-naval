package battleship.model;

/**
 * A ship in the fleet.
 *
 * Holds the name, size and hit points (HP). HP starts equal to the size
 * and goes down on every {@link #hit()}. When it reaches zero,
 * {@link #isSunk()} returns {@code true}.
 */
public class Ship {

    private final String name;
    private final int    size;
    private       int    hp;

    /**
     * Creates a ship with starting HP equal to its size.
     *
     * @param name display name (e.g. "Carrier")
     * @param size number of cells the ship takes
     */
    public Ship(String name, int size) {
        if (size <= 0) throw new IllegalArgumentException("Size must be positive: " + size);
        this.name = name;
        this.size = size;
        this.hp   = size;
    }

    public String  getName()  { return name; }
    public int     getSize()  { return size; }
    public int     getHp()    { return hp;   }
    public boolean isSunk()   { return hp <= 0; }

    /** Applies a hit. HP doesn't go below zero. */
    public void hit() {
        if (hp > 0) hp--;
    }
}
