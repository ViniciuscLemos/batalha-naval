package battleship.model;

// the sunk ship's name comes from Fleet.shipNameAt (an enum shouldn't hold state)
public enum ShotResult {
    MISS,
    HIT,
    SUNK
}
