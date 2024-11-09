package saka1029.executable;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static saka1029.executable.Helper.*;

public class TestFrame {

    Parser p = new Parser();
    Context c = Context.of();

    Executable eval(String s) {
        return c.eval(p.parse(s));
    }

    @Test
    public void testArguments() {
        assertEquals(i(3), eval("[a b - r : a b +] = plus stack 1 2 stack plus"));
    }

}
