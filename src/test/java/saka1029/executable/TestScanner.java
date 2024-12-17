package saka1029.executable;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import static saka1029.executable.Helper.*;

public class TestScanner {

    @Test
    public void testInt() {
        Scanner s = Scanner.of("  1 \t 22 -35   ");
        assertEquals(TokenType.NUMBER, s.get()); assertEquals(i(1), s.number());
        assertEquals(TokenType.NUMBER, s.get()); assertEquals(i(22), s.number());
        assertEquals(TokenType.NUMBER, s.get()); assertEquals(i(-35), s.number());
        assertEquals(TokenType.END, s.get());
    }

    @Test
    public void testMinus() {
        Scanner s = Scanner.of("  -1-ab -  ");
        assertEquals(TokenType.NUMBER, s.get()); assertEquals(i(-1), s.number());
        assertEquals(TokenType.SYMBOL, s.get()); assertEquals("-ab", s.string());
        assertEquals(TokenType.SYMBOL, s.get()); assertEquals("-", s.string());
        assertEquals(TokenType.END, s.get());
    }

    @Test
    public void testFrame() {
        Scanner s = Scanner.of("[a.b:r 3 +]");
        assertEquals(TokenType.LB, s.get()); assertEquals("[", s.string());
        assertEquals(TokenType.SYMBOL, s.get()); assertEquals("a", s.string());
        assertEquals(TokenType.DOT, s.get()); assertEquals(".", s.string());
        assertEquals(TokenType.SYMBOL, s.get()); assertEquals("b", s.string());
        assertEquals(TokenType.COLON, s.get()); assertEquals(":", s.string());
        assertEquals(TokenType.SYMBOL, s.get()); assertEquals("r", s.string());
        assertEquals(TokenType.NUMBER, s.get()); assertEquals(i(3), s.number());
        assertEquals(TokenType.SYMBOL, s.get()); assertEquals("+", s.string());
        assertEquals(TokenType.RB, s.get()); assertEquals("]", s.string());
        assertEquals(TokenType.END, s.get());
    }

}
