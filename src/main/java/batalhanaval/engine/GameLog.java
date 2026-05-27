package batalhanaval.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Registra e expõe os eventos de uma partida.
 *
 * <p>Os eventos são armazenados em ordem cronológica. Métodos de impressão
 * permitem exibir o log completo ou apenas a cauda.</p>
 */
public class GameLog {

    private final List<String> entries = new ArrayList<>();

    /** Adiciona um novo evento ao log. */
    public void add(String event) {
        entries.add(event);
    }

    /** Retorna uma visão imutável de todos os eventos. */
    public List<String> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    /**
     * Imprime os últimos {@code n} eventos no stdout.
     *
     * @param n número de eventos a exibir (se o log tiver menos, exibe todos)
     */
    public void printTail(int n) {
        System.out.println("--- Últimos eventos ---");
        int start = Math.max(0, entries.size() - n);
        for (int i = start; i < entries.size(); i++) {
            System.out.printf("%3d) %s%n", i + 1, entries.get(i));
        }
    }

    /** Imprime todos os eventos no stdout. */
    public void printAll() {
        System.out.println("--- Log completo ---");
        for (int i = 0; i < entries.size(); i++) {
            System.out.printf("%3d) %s%n", i + 1, entries.get(i));
        }
    }
}
