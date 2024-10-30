package saka1029.executable;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static saka1029.executable.Helper.*;

import java.beans.Transient;

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
        run("1 = v");
        run("v 1 + ! v");
        assertEquals(i(2), eval("v"));
        try {
            run("v ! w");
            fail();
        } catch (RuntimeException e) {
            assertEquals("Symbol 'w' not defined", e.getMessage());
        }
    }

    @Test
    public void testDefineFact() {
        run("(dup 0 <= (drop 1) (dup 1 - fact *) if) = fact");
        assertEquals(i(1), eval("0 fact"));
        assertEquals(i(1), eval("1 fact"));
        assertEquals(i(2), eval("2 fact"));
        assertEquals(i(6), eval("3 fact"));
        assertEquals(i(24), eval("4 fact"));
    }

    @Test
    public void testDefineSum() {
        run("(0 swap (+) for) = sum");
        assertEquals(i(0), eval("() sum"));
        assertEquals(i(1), eval("(1) sum"));
        assertEquals(i(3), eval("(1 2) sum"));
        assertEquals(i(6), eval("(1 2 3) sum"));
    }

    @Test
    public void testDefineReverse() {
        run("(() swap (rcons) for) = reverse");
        assertEquals(NIL, eval("() reverse"));
        assertEquals(eval("(1)"), eval("(1) reverse"));
        assertEquals(eval("(2 1)"), eval("(1 2) reverse"));
        assertEquals(eval("(3 2 1)"), eval("(1 2 3) reverse"));
        assertEquals(eval("(3 (2 20) 1)"), eval("(1 (2 20) 3) reverse"));
    }

    @Test
    public void testReverseByRecursion() {
        run("(() reverse2) = reverse");
        run("(dup1 null (swap drop) (swap uncons swap rot cons reverse2) if) = reverse2");
        assertEquals(NIL, eval("() reverse"));
        assertEquals(eval("(1)"), eval("(1) reverse"));
        assertEquals(eval("(2 1)"), eval("(1 2) reverse"));
        assertEquals(eval("(3 2 1)"), eval("(1 2 3) reverse"));
        assertEquals(eval("(3 (2 20) 1)"), eval("(1 (2 20) 3) reverse"));
    }

    @Test
    public void testDefineAppend() {
        run("(swap dup null (drop) (uncons rot append cons) if) = append");
        assertEquals(NIL, eval("() () append"));
        assertEquals(eval("(a)"), eval("() (a) append"));
        assertEquals(eval("(a b)"), eval("() (a b) append"));
        assertEquals(eval("(x a b)"), eval("(x) (a b) append"));
        assertEquals(eval("(x y a b)"), eval("(x y) (a b) append"));
        assertEquals(eval("(x y)"), eval("(x y) () append"));
    }

    @Test
    public void testFactByRange() {
        run("(1 swap 1 swap 1 range (*) for) = fact");
        assertEquals(i(1), eval("0 fact"));
        assertEquals(i(1), eval("1 fact"));
        assertEquals(i(2), eval("2 fact"));
        assertEquals(i(6), eval("3 fact"));
        assertEquals(i(24), eval("4 fact"));
    }

}
