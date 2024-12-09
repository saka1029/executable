package saka1029.executable;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static saka1029.executable.Helper.*;

public class TestEval {

    Parser p = new Parser();
    Context c = Context.of();

    List parse(String input) {
        return p.parse(input);
    }

    void run(String input) {
        c.run(parse(input));
    }

    Executable eval(String input) {
        return c.eval(parse(input));
    }

    @Test
    public void testDefineSet() {
        run("variable v 1");
        run("v 1 + set v");
        assertEquals(i(2), eval("v"));
        try {
            run("v set w");
            fail();
        } catch (RuntimeException e) {
            assertEquals("Symbol 'w' not defined", e.getMessage());
        }
    }

    @Test
    public void testDefineFunction() {
        run("function list '(1 2 3)");
        assertEquals(list(i(1), i(2), i(3)), eval("list '() cons cons cons"));
    }

    @Test
    public void testDefineVariable() {
        run("variable list '(1 2 3)");
        assertEquals(list(i(1), i(2), i(3)), eval("list"));
    }

    @Test
    public void testCall() {
        assertEquals(i(3), eval("'(1 2 +) call"));
    }

    @Test
    public void testDefineFact() {
        run("function fact '(dup 0 <= '(drop 1) '(dup 1 - fact *) if)");
        assertEquals(i(1), eval("0 fact"));
        assertEquals(i(1), eval("1 fact"));
        assertEquals(i(2), eval("2 fact"));
        assertEquals(i(6), eval("3 fact"));
        assertEquals(i(24), eval("4 fact"));
    }

    @Test
    public void testDefineSum() {
        run("function sum '(0 swap '+ for)");
        assertEquals(i(0), eval("'() sum"));
        assertEquals(i(1), eval("'(1) sum"));
        assertEquals(i(3), eval("'(1 2) sum"));
        assertEquals(i(6), eval("'(1 2 3) sum"));
    }

    @Test
    public void testDefineReverse() {
        run("function reverse '('() swap 'rcons for)");
        assertEquals(NIL, eval("'() reverse"));
        assertEquals(eval("'(1)"), eval("'(1) reverse"));
        assertEquals(eval("'(2 1)"), eval("'(1 2) reverse"));
        assertEquals(eval("'(3 2 1)"), eval("'(1 2 3) reverse"));
        assertEquals(eval("'(3 (2 20) 1)"), eval("'(1 (2 20) 3) reverse"));
    }

    @Test
    public void testReverseByRecursion() {
        run("function reverse '('() reverse2)");
        run("function reverse2 '(dup1 null '(swap drop) '(swap uncons swap rot cons reverse2) if)");
        assertEquals(NIL, eval("'() reverse"));
        assertEquals(eval("'(1)"), eval("'(1) reverse"));
        assertEquals(eval("'(2 1)"), eval("'(1 2) reverse"));
        assertEquals(eval("'(3 2 1)"), eval("'(1 2 3) reverse"));
        assertEquals(eval("'(3 (2 20) 1)"), eval("'(1 (2 20) 3) reverse"));
    }

    @Test
    public void testDefineAppend() {
        run("function append '(swap dup null 'drop '(uncons rot append cons) if)");
        assertEquals(NIL, eval("'() '() append"));
        assertEquals(eval("'(a)"), eval("'() '(a) append"));
        assertEquals(eval("'(a b)"), eval("'() '(a b) append"));
        assertEquals(eval("'(x a b)"), eval("'(x) '(a b) append"));
        assertEquals(eval("'(x y a b)"), eval("'(x y) '(a b) append"));
        assertEquals(eval("'(x y)"), eval("'(x y) '() append"));
    }

    @Test
    public void testFactByRange() {
        run("function fact '(1 swap 1 swap 1 range '* for)");
        assertEquals(i(1), eval("0 fact"));
        assertEquals(i(1), eval("1 fact"));
        assertEquals(i(2), eval("2 fact"));
        assertEquals(i(6), eval("3 fact"));
        assertEquals(i(24), eval("4 fact"));
    }

    static float Q_rsqrt(float number) {
        final float threehalfs = 1.5F;
        float x2 = number * 0.5F, y = number;
        long i = Float.floatToIntBits(y);
        i = 0x5f3759df - (i >> 1);
        y = Float.intBitsToFloat((int)i);
        y = y * (threehalfs - (x2 * y * y));
        y = y * (threehalfs - (x2 * y * y));
        return y;
    }

    @Test
    public void TestQ_rsqrt() {
        assertEquals((float)(1 / Math.sqrt(3)), Q_rsqrt(3F), 5E-6);
        assertEquals((float)(1 / Math.sqrt(71)), Q_rsqrt(71F), 5E-6);
    }

}
