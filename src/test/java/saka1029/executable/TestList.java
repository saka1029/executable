package saka1029.executable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import static saka1029.executable.Helper.*;

import java.util.Iterator;

public class TestList {

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

}
