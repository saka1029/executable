package saka1029.executable;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static saka1029.executable.Helper.*;

import java.util.Iterator;

public class TestFrame {

    Parser p = new Parser();
    Context c = Context.of();

    void run(String s) {
        c.run(p.parse(s));
    }

    Executable eval(String s) {
        return c.eval(p.parse(s));
    }

    @Test
    public void testFrameIterator() {
        java.util.List<String> list = java.util.List.of("1", "2", "3");
        Iterator<String> iterator = new Iterator<>() {
            int index = 0, size = list.size() + 2;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public String next() {
                return ++index == 1 ? "A"
                    : index == size ? "Z"
                    : list.get(index - 2);
            }
        };
        assertEquals("A", iterator.next());
        assertEquals("1", iterator.next());
        assertEquals("2", iterator.next());
        assertEquals("3", iterator.next());
        assertEquals("Z", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testArguments() {
        assertEquals(i(3), eval("'[a b - r : a b +] = plus 1 2 plus"));
    }

    @Test
    public void testCall() {
        assertEquals(i(3), eval("1 2 [a b - r : a b +]"));
    }

    @Test
    public void testLocalFunction() {
        assertEquals(i(25), eval("'[a b - r : '(dup *) = double a double b double +] = hypot 3 4 hypot"));
    }

    @Test
    public void testLocalFrame() {
        assertEquals(i(25), eval("'[a b - r : '[a - r : a a *] = double a double b double +] = hypot 3 4 hypot"));
    }

    @Test
    public void testNoArgumentOneReturn() {
        assertEquals(i(123), eval("'[ - r : 123] = value value"));
    }

    @Test
    public void testNoArgumentTwoReturn() {
        assertEquals(i(579), eval("'[ - r s : 123 456] = value value +"));
    }

    @Test
    public void testTwoArgumentTwoReturn() {
        assertEquals(i(5), eval("'[a b - r s : a b / a b %] = div 11 3 div stack +"));
    }

    @Test
    public void testSetLocal() {
        // assertEquals(i(10), eval("'[list - r : 0 = sum list '(sum + ! sum) for sum] = sum '(1 2 3 4) sum"));
        assertEquals(i(10), eval("'[list - r : 0 = acc list '(acc + ! acc) for acc] = sum '(1 2 3 4) sum"));
    }

    @Test
    public void testSetLocalReverse() {
        assertEquals(list(), eval("'()"));
        assertEquals(list(i(4), i(3), i(2), i(1)),
            eval("'[list - r : '() = acc list '(acc stack cons ! acc) for acc] = reverse '(1 2 3 4) reverse"));
    }

    @Test
    public void testSelf() {
        assertEquals(i(120 ), eval("5 [n - r : n 0 <= 1 (n 1 - self n *) if] call"));
    }

    @Test
    public void testRecursion() {
        run("[n - r : n 0 <= 1 (n 1 - fact n *) if] = fact");
        assertEquals(i(1), eval("0 fact"));
        assertEquals(i(1), eval("1 fact"));
        assertEquals(i(2), eval("2 fact"));
        assertEquals(i(6), eval("3 fact"));
        assertEquals(i(24), eval("4 fact"));
        assertEquals(i(120), eval("5 fact"));
    }

}
