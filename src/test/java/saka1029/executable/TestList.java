package saka1029.executable;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestList {

    @Test
    public void testNIL() {
        assertEquals("()", List.NIL.toString());
    }

    @Test
    public void testCons() {
        assertEquals("(a b)", Cons.of(Symbol.of("a"), Symbol.of("b")).toString());
    }

}
