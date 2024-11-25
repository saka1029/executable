package saka1029.executable;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static saka1029.executable.Helper.*;

public class TestHelper {

    @Test
    public void testAsI() {
        assertEquals(3, asI(i(3), "testAsI"));
        try {
            asI(list(sym("a")), "testAsI");
        } catch (RuntimeException e) {
            assertEquals("testAsI: '(a)' is not int", e.getMessage());
        }
    }

    @Test
    public void testAsComp() {
        assertEquals(i(3), asComp(i(3), "testAsComp"));
        try {
            asComp(list(sym("a")), "testAsComp");
        } catch (RuntimeException e) {
            assertEquals("testAsComp: '(a)' is not comparable", e.getMessage());
        }
    }

}
