package saka1029.executable;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static saka1029.executable.Helper.*;

public class TestParser {

    @Test
    public void testElement() {
        Parser p = new Parser();
        assertEquals(list(sym("a")), p.parse("a"));
        assertEquals(list(TRUE), p.parse("true"));
        assertEquals(list(FALSE), p.parse("false"));
        assertEquals(list(NIL), p.parse("()"));
    }

}
