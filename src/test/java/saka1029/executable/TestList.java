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
        assertEquals("(a b)", list(sym("a"), sym("b")).toString());
    }

    @Test
    public void testIterator() {
        List list = list(i(1), i(2), sym("+"));
        Iterator<Executable> it = list.iterator();
        assertEquals(i(1), it.next());
        assertEquals(i(2), it.next());
        assertEquals(sym("+"), it.next());
        assertFalse(it.hasNext());
    }
}
