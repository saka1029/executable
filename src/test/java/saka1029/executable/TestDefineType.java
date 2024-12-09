package saka1029.executable;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestDefineType {

    @Test
    public void testToString() {
        assertEquals("function", DefineType.FUNCTION.toString());
        assertEquals("function", "" + DefineType.FUNCTION);
        assertEquals("variable", DefineType.VARIABLE.toString());
        assertEquals("variable", "" + DefineType.VARIABLE);
    }
}
