package saka1029.executable;

import static org.junit.Assert.assertEquals;
import static saka1029.executable.Helper.*;

import saka1029.Common;
import java.util.logging.Logger;

import org.junit.Test;

public class TestContext {

    static final Logger logger = Common.logger(TestContext.class);

    @Test
    public void testIfInt() {
        Context c = Context.of();
        assertEquals(i(0), c.eval(l(TRUE, i(0), i(1), s("if"))));
        assertEquals(i(1), c.eval(l(FALSE, i(0), i(1), s("if"))));
    }

    public void testIfList() {
        Context c = Context.of();
        assertEquals(i(3), c.eval(l(TRUE,
            l(i(1), i(2), s("+")),
            l(i(2), i(3), s("+")), s("if"))));
        assertEquals(i(5), c.eval(l(FALSE,
            l(i(1), i(2), s("+")),
            l(i(2), i(3), s("+")), s("if"))));
    }

    @Test
    public void testDefineInt() {
        Context c = Context.of();
        c.run(l(i(9), define(s("nine"))));
        assertEquals(i(9), c.eval(l(s("nine"))));
    }

    @Test
    public void testDefineList() {
        Context c = Context.of();
        c.run(l(l(s("dup"), s("*")), define(s("square"))));
        assertEquals(i(9), c.eval(l(i(3), s("square"))));
    }
}
