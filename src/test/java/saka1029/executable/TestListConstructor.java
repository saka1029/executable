package saka1029.executable;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestListConstructor {

    Parser p = new Parser();

    void run(Context c, String s) {
        c.run(p.parse(s));
    }

    Executable eval(Context c, String s) {
        return c.eval(p.parse(s));
    }

    @Test
    public void testList() {
        Context c = Context.of();
        assertEquals(eval(c, "'(1 2 3)"), eval(c, "`(1 2 3)"));
    }

    @Test
    public void testSimpleExpression() {
        Context c = Context.of();
        assertEquals(eval(c, "'(1 2 3)"), eval(c, "`(1 1 1 + 1 1 1 + +)"));
    }

    @Test
    public void testNest() {
        Context c = Context.of();
        assertEquals(eval(c, "'((1 2) 3)"), eval(c, "`(`(1 1 1 +) 1 1 1 + +)"));
        assertEquals(eval(c, "'((1 1 1 +) 3)"), eval(c, "`('(1 1 1 +) 1 1 1 + +)"));
    }

    @Test
    public void testValue() {
        Context c = Context.of();
        assertEquals(eval(c, "'(1)"), eval(c, "`1"));
        assertEquals(eval(c, "'(1)"), eval(c, "`(1)"));
        assertEquals(eval(c, "'()"), eval(c, "`()"));
        assertEquals(eval(c, "'(())"), eval(c, "`nil"));
    }

    @Test
    public void testRange() {
        Context c = Context.of();
        assertEquals(eval(c, "'(1 2 3)"), eval(c, "`(1 3 1 range nil for)"));
    }

}
