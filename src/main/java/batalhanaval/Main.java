package batalhanaval;

import batalhanaval.cpu.HuntTargetStrategy;

import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

/**
 * Ponto de entrada da aplicação Batalha Naval.
 *
 * <p>Lê a seed opcional, cria as dependências e inicia a partida.</p>
 *
 * <h2>Como compilar e executar</h2>
 * <pre>
 *   # A partir da raiz do projeto
 *   javac -d out $(find src/main/java -name "*.java")
 *   java -cp out batalhanaval.Main
 * </pre>
 *
 * <p>Ou com Maven (se o pom.xml estiver configurado):</p>
 * <pre>
 *   mvn compile exec:java -Dexec.mainClass=batalhanaval.Main
 * </pre>
 */
public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Seed (vazio para aleatório): ");
        String seedStr = sc.nextLine().trim();
        Random rng;
        if (seedStr.isEmpty()) {
            rng = new Random();
        } else {
            long seed;
            try { seed = Long.parseLong(seedStr); }
            catch (NumberFormatException e) { seed = seedStr.hashCode(); }
            rng = new Random(seed);
        }

        try {
            new Game(sc, rng, new HuntTargetStrategy(rng)).run();
        } catch (NoSuchElementException e) {
            // Ctrl+D / Ctrl+Z ou fim da entrada redirecionada
            System.out.println("\nEntrada encerrada. Até a próxima!");
        } finally {
            sc.close();
        }
    }
}
