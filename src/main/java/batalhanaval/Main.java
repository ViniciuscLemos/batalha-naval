package batalhanaval;

import batalhanaval.cpu.HuntTargetStrategy;

import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

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
            // Ctrl+D / Ctrl+Z
            System.out.println("\nEntrada encerrada. Até a próxima!");
        } finally {
            sc.close();
        }
    }
}
