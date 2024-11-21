package saka1029.executable;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static saka1029.executable.Helper.*;

public class TestParser {

    Parser p = new Parser();

    @Test
    public void testSymbol() {
        assertEquals(list(sym("a")), p.parse("a"));
    }

    @Test
    public void testConstant() {
        assertEquals(list(TRUE), p.parse("true"));
        assertEquals(list(FALSE), p.parse("false"));
    }

    @Test
    public void testList() {
        assertEquals(list(NIL), p.parse("()"));
        assertEquals(list(sym("a"), i(0), sym("<"), list(i(0), sym("a"), sym("-")), list(sym("a")), sym("if")),
            p.parse("a 0 < (0 a -) (a) if"));
    }

}
