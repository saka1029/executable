package saka1029.executable;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static saka1029.executable.Helper.*;

import java.util.Iterator;

public class TestFrame {

    Parser p = new Parser();
    Context c = Context.of();

    Executable eval(String s) {
        return c.eval(p.parse(s));
    }

    @Test
    public void testFrameIterator() {
        java.util.List<String> list = java.util.List.of("1", "2", "3");
        Iterator<String> iterator = new Iterator<>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < list.size() + 2;
            }

            @Override
            public String next() {
                String result;
                if (index == 0)
                    result = "A";
                else if (index == list.size() + 1)
                    result = "Z";
                else
                    result = list.get(index - 1);
                ++index;
                return result;
            }
        };
        assertEquals("A", iterator.next());
        assertEquals("1", iterator.next());
        assertEquals("2", iterator.next());
        assertEquals("3", iterator.next());
        assertEquals("Z", iterator.next());
        assertFalse(iterator.hasNext());
    }


    // @Test
    public void testArguments() {
        assertEquals(i(3), eval("[a b - r : a b +] = plus stack 1 2 stack plus"));
    }

}
