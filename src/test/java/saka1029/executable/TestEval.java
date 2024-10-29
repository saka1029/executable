package saka1029.executable;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static saka1029.executable.Helper.*;

public class TestEval {

    Parser p = new Parser();

    List parse(String input) {
        return p.parse(input);
    }

    Executable eval(Context c, String input) {
        return c.eval(parse(input));
    }

    @Test
    public void testDefineFact() {
        Context c = Context.of();
        c.run(parse("(dup 0 <= (drop 1) (dup 1 - *) if) = fact"));
        assertEquals(i(1), eval(c, "0 fact"));
    }

}
