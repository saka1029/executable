package saka1029.executable;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static saka1029.executable.Helper.*;

import java.util.Iterator;

public class TestRange {

    @Test
    public void testAscending() {
        Range r = Range.of(0, 4 , 2);
        Iterator<Executable> i = r.iterator();
        assertEquals(i(0), i.next());
        assertEquals(i(2), i.next());
        assertEquals(i(4), i.next());
        assertFalse(i.hasNext());
    }

    @Test
    public void testDescending() {
        Range r = Range.of(4, 0 , -2);
        Iterator<Executable> i = r.iterator();
        assertEquals(i(4), i.next());
        assertEquals(i(2), i.next());
        assertEquals(i(0), i.next());
        assertFalse(i.hasNext());
    }

}
