package battleship.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Records the events of a match.
 *
 * Events are kept in chronological order. The print methods show
 * the full log or just the tail.
 */
public class GameLog {

    private final List<String> entries = new ArrayList<>();

    /** Adds a new event to the log. */
    public void add(String event) {
        entries.add(event);
    }

    /** Returns a read-only view of all events. */
    public List<String> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    /**
     * Prints the last {@code n} events to stdout.
     *
     * @param n number of events to show (if the log has fewer, shows them all)
     */
    public void printTail(int n) {
        System.out.println("--- Latest events ---");
        int start = Math.max(0, entries.size() - n);
        for (int i = start; i < entries.size(); i++) {
            System.out.printf("%3d) %s%n", i + 1, entries.get(i));
        }
    }

    /** Prints all events to stdout. */
    public void printAll() {
        System.out.println("--- Full log ---");
        for (int i = 0; i < entries.size(); i++) {
            System.out.printf("%3d) %s%n", i + 1, entries.get(i));
        }
    }
}
