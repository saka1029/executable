package saka1029.executable;

import static org.junit.Assert.assertEquals;
import static saka1029.executable.Helper.*;

import saka1029.Common;

import java.util.logging.Logger;

import org.junit.Test;

public class TestContext {

    static final Logger logger = Common.logger(TestContext.class);

    @Test
    public void testRun() {
        Context c = Context.of();
        List list = list(i(1), i(2), sym("+"));
        c.run(list);
        assertEquals(1, c.stack.size());
        assertEquals(i(3), c.pop());
    }

    @Test
    public void testCall() {
        Context c = Context.of();
        List list = list(list(i(3)), sym("call"));
        c.run(list);
        assertEquals(1, c.stack.size());
        assertEquals(i(3), c.pop());
    }

    @Test
    public void testRot() {
        Context c = Context.of();
        c.push(i(1));
        c.push(i(2));
        c.push(i(3));
        c.rot();
        assertEquals(i(1), c.pop());
        assertEquals(i(3), c.pop());
        assertEquals(i(2), c.pop());
    }

    @Test
    public void testIfInt() {
        Context c = Context.of();
        assertEquals(i(0), c.eval(list(TRUE, i(0), i(1), sym("if"))));
        assertEquals(i(1), c.eval(list(FALSE, i(0), i(1), sym("if"))));
    }

    public void testIfList() {
        Context c = Context.of();
        assertEquals(i(3), c.eval(list(TRUE,
            list(i(1), i(2), sym("+")),
            list(i(2), i(3), sym("+")), sym("if"))));
        assertEquals(i(5), c.eval(list(FALSE,
            list(i(1), i(2), sym("+")),
            list(i(2), i(3), sym("+")), sym("if"))));
    }

    @Test
    public void testDefineInt() {
        Context c = Context.of();
        c.run(list(i(9), defvar(sym("nine"))));
        assertEquals(i(9), c.eval(list(sym("nine"))));
        assertEquals("9", c.globals.get(sym("nine")).value.toString());
        assertEquals(false, c.globals.get(sym("nine")).isFunction);
    }

    @Test
    public void testDefineList() {
        Context c = Context.of();
        c.run(list(quote(list(sym("dup"), sym("*"))), defun(sym("square"))));
        assertEquals(i(9), c.eval(list(i(3), sym("square"))));
        assertEquals("(dup *)", c.globals.get(sym("square")).value.toString());
        assertEquals(true, c.globals.get(sym("square")).isFunction);
    }

    @Test
    public void testFor() {
        Context c = Context.of();
        List list = list(quote(list(i(1), i(2), i(3))), quote(sym("print")), sym("for"));
        // Common.log(logger, Level.INFO, "list=%s", list);
        c.run(list);
    }

    @Test
    public void testCons() {
        Context c = Context.of();
        assertEquals(list(i(1), i(2), i(3)), c.eval(list(i(1), quote(list(i(2), i(3))), sym("cons"))));
    }

    @Test
    public void testRcons() {
        Context c = Context.of();
        assertEquals(list(i(1), i(2), i(3)), c.eval(list(quote(list(i(2), i(3))), i(1), sym("rcons"))));
    }

    @Test
    public void testReverseByFor() {
        Context c = Context.of();
        List list = list(quote(NIL), quote(list(i(1), i(2), i(3))), quote(sym("rcons")), sym("for"));
        // Common.log(logger, Level.INFO, "list=%s", list);
        assertEquals(list(i(3), i(2), i(1)), c.eval(list));
    }

    @Test
    public void testDefineReverseByFor() {
        Context c = Context.of();
        c.run(list(quote(list(quote(NIL), sym("swap"), quote(sym("rcons")), sym("for"))), defun(sym("reverse"))));
        assertEquals(list(i(3), i(2), i(1)), c.eval(list(quote(list(i(1), i(2), i(3))), sym("reverse"))));
    }

    @Test
    public void testDefineSet() {
        Context c = Context.of();
        c.run(list(i(9), defvar(sym("nine"))));
        assertEquals(i(9), c.eval(list(sym("nine"))));
        c.run(list(i(99), set(sym("nine"))));
        assertEquals(i(99), c.eval(list(sym("nine"))));
    }

    @Test
    public void testReverse() {
        Context c = Context.of();
        assertEquals(NIL, c.eval(list(quote(NIL), sym("reverse"))));
        assertEquals(list(i(3), i(2), i(1)), c.eval(list(quote(list(i(1), i(2), i(3))), sym("reverse"))));
    }
}
