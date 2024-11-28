package saka1029.executable;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static saka1029.executable.Helper.*;

import java.util.Iterator;

public class TestArray {

    @Test
    public void testSize() {
        Array a = Array.of(4, NIL);
        assertEquals(4, a.size());
    }

    @Test
    public void testGetSet() {
        Array a = Array.of(4, i(0));
        assertEquals(i(0), a.get(1));
        a.put(1, i(3));
        assertEquals(i(3), a.get(1));
    }

    @Test
    public void testIterator() {
        Array a = Array.of(i(0), i(1), i(2));
        Iterator<Executable> it = a.iterator();
        assertEquals(i(0), it.next());
        assertEquals(i(1), it.next());
        assertEquals(i(2), it.next());
        assertFalse(it.hasNext());
    }
}
