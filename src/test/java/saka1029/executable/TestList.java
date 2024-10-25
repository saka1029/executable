package saka1029.executable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import static saka1029.executable.Helper.*;

import java.util.Iterator;

public class TestList {

    static Context c = Context.of();

    @Test
    public void testNIL() {
        assertEquals("()", NIL.toString());
    }

    @Test
    public void testCons() {
        assertEquals("(a b)", Cons.of(s("a"), s("b")).toString());
    }

    @Test
    public void testIterator() {
        List list = l(i(1), i(2), s("+"));
        Iterator<Executable> it = list.iterator();
        assertEquals(i(1), it.next());
        assertEquals(i(2), it.next());
        assertEquals(s("+"), it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void testRun() {
        List list = l(i(1), i(2), s("+"));
        c.run(list);
        assertEquals(1, c.stack.size());
        assertEquals(i(3), c.pop());
    }

    @Test
    public void testCall() {
        List list = l(l(i(1)), s("call"));
        c.run(list);
        assertEquals(1, c.stack.size());
        assertEquals(i(3), c.pop());
    }

}
