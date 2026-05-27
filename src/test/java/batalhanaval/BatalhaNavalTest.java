package batalhanaval;

import batalhanaval.engine.CoordParser;
import batalhanaval.model.*;

/**
 * Testes unitários simples (sem framework externo).
 *
 * Execute com:
 *   javac -d out $(find src -name "*.java")
 *   java -cp out batalhanaval.BatalhaNavalTest
 */
public class BatalhaNavalTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testCoordParserValid();
        testCoordParserInvalid();
        testCoordFormat();
        testShipHit();
        testShipSunk();
        testBoardSetGet();
        testFleetCanPlace();
        testFleetReceiveShot();
        testFleetAllSunk();

        System.out.printf("%nResultado: %d passou(aram), %d falhou(aram).%n", passed, failed);
        if (failed > 0) System.exit(1);
    }

    // ------------------------------------------------------------------
    //  CoordParser
    // ------------------------------------------------------------------

    static void testCoordParserValid() {
        assertArrayEquals("parse A1",  new int[]{0, 0},  CoordParser.parse("A1"));
        assertArrayEquals("parse J10", new int[]{9, 9},  CoordParser.parse("J10"));
        assertArrayEquals("parse a1 (lower)", new int[]{0, 0}, CoordParser.parse("a1"));
        assertArrayEquals("parse E5",  new int[]{4, 4},  CoordParser.parse("E5"));
    }

    static void testCoordParserInvalid() {
        assertNull("null input",   CoordParser.parse(null));
        assertNull("empty string", CoordParser.parse(""));
        assertNull("K1 out of col", CoordParser.parse("K1"));
        assertNull("A0 out of row", CoordParser.parse("A0"));
        assertNull("A11 out of row", CoordParser.parse("A11"));
        assertNull("letters only",  CoordParser.parse("AB"));
    }

    static void testCoordFormat() {
        assertEquals("format 0,0", "A1",  CoordParser.format(0, 0));
        assertEquals("format 9,9", "J10", CoordParser.format(9, 9));
    }

    // ------------------------------------------------------------------
    //  Ship
    // ------------------------------------------------------------------

    static void testShipHit() {
        Ship s = new Ship("Test", 3);
        s.hit();
        assertEquals("hp after 1 hit", 2, s.getHp());
        assertFalse("not sunk after 1 hit", s.isSunk());
    }

    static void testShipSunk() {
        Ship s = new Ship("Test", 2);
        s.hit(); s.hit();
        assertTrue("sunk after 2 hits", s.isSunk());
        // HP não deve ir abaixo de 0
        s.hit();
        assertEquals("hp >= 0 after extra hit", 0, s.getHp());
    }

    // ------------------------------------------------------------------
    //  Board
    // ------------------------------------------------------------------

    static void testBoardSetGet() {
        Board b = new Board();
        assertEquals("initial cell is EMPTY", Board.Cell.EMPTY, b.get(0, 0));
        b.set(0, 0, Board.Cell.SHIP);
        assertEquals("cell is SHIP after set", Board.Cell.SHIP, b.get(0, 0));
    }

    // ------------------------------------------------------------------
    //  Fleet
    // ------------------------------------------------------------------

    static void testFleetCanPlace() {
        Board b = new Board();
        Ship[] ships = {new Ship("T", 3)};
        Fleet f = new Fleet(b, ships);

        assertTrue("can place horizontal",  f.canPlace(0, 0, 3, true));
        assertFalse("cannot place out of bounds", f.canPlace(0, 8, 3, true));
    }

    static void testFleetReceiveShot() {
        Board b = new Board();
        Ship[] ships = {new Ship("Destroyer", 2)};
        Fleet f = new Fleet(b, ships);
        f.place(0, 0, 0, true); // ocupa (0,0) e (0,1)

        ShotResult miss = f.receiveShot(5, 5);
        assertEquals("shot at empty = MISS", ShotResult.MISS, miss);

        ShotResult hit = f.receiveShot(0, 0);
        assertEquals("shot at ship = HIT", ShotResult.HIT, hit);
    }

    static void testFleetAllSunk() {
        Board b = new Board();
        Ship[] ships = {new Ship("Tiny", 1)};
        Fleet f = new Fleet(b, ships);
        f.place(0, 0, 0, true);

        assertFalse("not all sunk yet", f.allSunk());
        f.receiveShot(0, 0);
        assertTrue("all sunk after last shot", f.allSunk());
    }

    // ------------------------------------------------------------------
    //  Assertions
    // ------------------------------------------------------------------

    static void assertEquals(String label, Object expected, Object actual) {
        if (expected.equals(actual)) {
            System.out.printf("  PASS  %s%n", label);
            passed++;
        } else {
            System.out.printf("  FAIL  %s — esperado <%s> mas foi <%s>%n", label, expected, actual);
            failed++;
        }
    }

    static void assertEquals(String label, int expected, int actual) {
        assertEquals(label, (Integer) expected, (Integer) actual);
    }

    static void assertTrue(String label, boolean cond) {
        assertEquals(label, true, cond);
    }

    static void assertFalse(String label, boolean cond) {
        assertEquals(label, false, cond);
    }

    static void assertNull(String label, Object obj) {
        if (obj == null) {
            System.out.printf("  PASS  %s%n", label);
            passed++;
        } else {
            System.out.printf("  FAIL  %s — esperado null mas foi <%s>%n", label, obj);
            failed++;
        }
    }

    static void assertArrayEquals(String label, int[] expected, int[] actual) {
        if (actual != null && actual.length == expected.length
                && actual[0] == expected[0] && actual[1] == expected[1]) {
            System.out.printf("  PASS  %s%n", label);
            passed++;
        } else {
            String e = expected == null ? "null" : "[" + expected[0] + "," + expected[1] + "]";
            String a = actual   == null ? "null" : "[" + actual[0]   + "," + actual[1]   + "]";
            System.out.printf("  FAIL  %s — esperado %s mas foi %s%n", label, e, a);
            failed++;
        }
    }
}
