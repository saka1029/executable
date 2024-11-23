package saka1029.executable;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestAlgorithm {

    Parser p = new Parser();
    Context c = Context.of();

    void run(String s) {
        c.run(p.parse(s));
    }

    Executable eval(String s) {
        return c.eval(p.parse(s));
    }

    @Test
    public void testReverseFor() {
        run("'('() swap 'rcons for) function reverse");
        assertEquals(eval("'()"), eval("'() reverse"));
        assertEquals(eval("'(1)"), eval("'(1) reverse"));
        assertEquals(eval("'(3 2 1)"), eval("'(1 2 3) reverse"));
        assertEquals(eval("'(3 (20 21) 1)"), eval("'(1 (20 21) 3) reverse"));
    }

    @Test
    public void testReverseForFrame() {
        run("'[i - r : '() variable a i '(a cons set a) for a] function reverse");
        assertEquals(eval("'()"), eval("'() reverse"));
        assertEquals(eval("'(1)"), eval("'(1) reverse"));
        assertEquals(eval("'(3 2 1)"), eval("'(1 2 3) reverse"));
        assertEquals(eval("'(3 (20 21) 1)"), eval("'(1 (20 21) 3) reverse"));
    }

    @Test
    public void testReverseFrameNest() {
        // run("'[i - r : '[i a - r : i null 'a '(i cdr i car a cons rev) if] function rev i '() rev] function reverse");
        run("'[i - r : '[i a - r : i null 'a '(i cdr i car a cons self) if] function rev i '() rev] function reverse");
        // run("'[i - r : i '() [i a - r : i null 'a '(i cdr i car a cons self) if]] function reverse");
        // run("'[i - r : '[i a - r : i null 'a '(i cdr i car a cons self) if] function rev i '() rev] function reverse");
        // run("'[i - r : '[i a - r : i null 'a '(i uncons swap a cons rev) if] function rev i '() rev] function reverse");
        assertEquals(eval("'()"), eval("'() reverse"));
        assertEquals(eval("'(1)"), eval("'(1) reverse"));
        assertEquals(eval("'(3 2 1)"), eval("'(1 2 3) reverse"));
        assertEquals(eval("'(3 (20 21) 1)"), eval("'(1 (20 21) 3) reverse"));
    }

    @Test
    public void testLength() {
        run("'(0 swap '(drop 1 +) for) function length");
        assertEquals(eval("0"), eval("'() length"));
        assertEquals(eval("1"), eval("'(2) length"));
        assertEquals(eval("2"), eval("'(1 2) length"));
    }

    @Test
    public void testLengthRecursive() {
        run("'(dup null 0 '(cdr length 1 +) if) function length");
        assertEquals(eval("0"), eval("'() length"));
        assertEquals(eval("1"), eval("'(2) length"));
        assertEquals(eval("2"), eval("'(1 2) length"));
    }

    @Test
    public void testLengthFrameFor() {
        run("'[i - r : 0 variable a i '(drop a 1 + set a) for a] function length");
        assertEquals(eval("0"), eval("'() length"));
        assertEquals(eval("1"), eval("'(2) length"));
        assertEquals(eval("2"), eval("'(1 2) length"));
    }

    @Test
    public void testLengthFrameRecursive() {
        run("'[i - r : i null 0 '(i cdr self 1 +) if] function length");
        assertEquals(eval("0"), eval("'() length"));
        assertEquals(eval("1"), eval("'(2) length"));
        assertEquals(eval("2"), eval("'(1 2) length"));
    }

    @Test
    public void testAppendRecursive() {
        run("'(swap dup null 'drop '(uncons rot append cons) if) function append");
        assertEquals(eval("'(3 4)"), eval("'() '(3 4) append"));
        assertEquals(eval("'(2 3 4)"), eval("'(2) '(3 4) append"));
        assertEquals(eval("'(1 2 3 4)"), eval("'(1 2) '(3 4) append"));
    }

    @Test
    public void testAppendRecursiveFrame() {
        run("'[a b - r : a null 'b '(a uncons b self cons) if] function append");
        assertEquals(eval("'(3 4)"), eval("'() '(3 4) append"));
        assertEquals(eval("'(2 3 4)"), eval("'(2) '(3 4) append"));
        assertEquals(eval("'(1 2 3 4)"), eval("'(1 2) '(3 4) append"));
    }

}
