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
        run("'[i - r : '[i a - r : i null 'a '(i cdr i car a cons rev) if] function rev i '() rev] function reverse");
        // run("'[i - r : i '() [i a - r : i null 'a '(i cdr i car a cons self) if]] function reverse");
        // run("'[i - r : '[i a - r : i null 'a '(i cdr i car a cons self) if] function rev i '() rev] function reverse");
        // run("'[i - r : '[i a - r : i null 'a '(i uncons swap a cons rev) if] function rev i '() rev] function reverse");
        assertEquals(eval("'()"), eval("'() reverse"));
        assertEquals(eval("'(1)"), eval("'(1) reverse"));
        assertEquals(eval("'(3 2 1)"), eval("'(1 2 3) reverse"));
        assertEquals(eval("'(3 (20 21) 1)"), eval("'(1 (20 21) 3) reverse"));
    }

}
