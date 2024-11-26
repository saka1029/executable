package saka1029.executable;

import static org.junit.Assert.assertEquals;
import static saka1029.executable.Helper.*;

import org.junit.Test;

public class TestAlgorithm {

    Parser p = new Parser();
    Context c = Context.of();

    void run(String s) {
        c.run(p.parse(s));
    }

    Executable eval(String s) {
        return c.eval(p.parse(s));
    }

    @Test
    public void testReverseFor() {
        run("'('() swap 'rcons for) function reverse");
        assertEquals(eval("'()"), eval("'() reverse"));
        assertEquals(eval("'(1)"), eval("'(1) reverse"));
        assertEquals(eval("'(3 2 1)"), eval("'(1 2 3) reverse"));
        assertEquals(eval("'(3 (20 21) 1)"), eval("'(1 (20 21) 3) reverse"));
    }

    @Test
    public void testReverseForFrame() {
        run("'[i - r : '() variable a i '(a cons set a) for a] function reverse");
        assertEquals(eval("'()"), eval("'() reverse"));
        assertEquals(eval("'(1)"), eval("'(1) reverse"));
        assertEquals(eval("'(3 2 1)"), eval("'(1 2 3) reverse"));
        assertEquals(eval("'(3 (20 21) 1)"), eval("'(1 (20 21) 3) reverse"));
    }

    @Test
    public void testReverseFrameNest() {
        run("'[i - r : '[i a - r : i null 'a '(i cdr i car a cons rev) if] function rev i '() rev] function reverse");
        // run("'[i - r : '[i a - r : i null 'a '(i uncons swap a cons rev) if] function rev i '() rev] function reverse");
        assertEquals(eval("'()"), eval("'() reverse"));
        assertEquals(eval("'(1)"), eval("'(1) reverse"));
        assertEquals(eval("'(3 2 1)"), eval("'(1 2 3) reverse"));
        assertEquals(eval("'(3 (20 21) 1)"), eval("'(1 (20 21) 3) reverse"));
    }

    @Test
    public void testReverseFrameNestDirect() {
        run("'[i - r : i '() [i a - r : i null 'a '(i cdr i car a cons self) if]] function reverse");
        assertEquals(eval("'()"), eval("'() reverse"));
        assertEquals(eval("'(1)"), eval("'(1) reverse"));
        assertEquals(eval("'(3 2 1)"), eval("'(1 2 3) reverse"));
        assertEquals(eval("'(3 (20 21) 1)"), eval("'(1 (20 21) 3) reverse"));
    }

    @Test
    public void testReverseFrameWhile() {
        run("'[i - r : '() '(i null not) '(i uncons set i rcons) while] function reverse");
        assertEquals(eval("'()"), eval("'() reverse"));
        assertEquals(eval("'(1)"), eval("'(1) reverse"));
        assertEquals(eval("'(3 2 1)"), eval("'(1 2 3) reverse"));
        assertEquals(eval("'(3 (20 21) 1)"), eval("'(1 (20 21) 3) reverse"));
    }

    @Test
    public void testLength() {
        run("'(0 swap '(drop 1 +) for) function length");
        assertEquals(eval("0"), eval("'() length"));
        assertEquals(eval("1"), eval("'(2) length"));
        assertEquals(eval("2"), eval("'(1 2) length"));
    }

    @Test
    public void testLengthRecursive() {
        run("'(dup null 0 '(cdr length 1 +) if) function length");
        assertEquals(eval("0"), eval("'() length"));
        assertEquals(eval("1"), eval("'(2) length"));
        assertEquals(eval("2"), eval("'(1 2) length"));
    }

    @Test
    public void testLengthFrameFor() {
        run("'[i - r : 0 variable a i '(drop a 1 + set a) for a] function length");
        assertEquals(eval("0"), eval("'() length"));
        assertEquals(eval("1"), eval("'(2) length"));
        assertEquals(eval("2"), eval("'(1 2) length"));
    }

    @Test
    public void testLengthFrameRecursive() {
        run("'[i - r : i null 0 '(i cdr self 1 +) if] function length");
        assertEquals(eval("0"), eval("'() length"));
        assertEquals(eval("1"), eval("'(2) length"));
        assertEquals(eval("2"), eval("'(1 2) length"));
    }

    @Test
    public void testAppendRecursive() {
        run("'(swap dup null 'drop '(uncons rot append cons) if) function append");
        assertEquals(eval("'(3 4)"), eval("'() '(3 4) append"));
        assertEquals(eval("'(2 3 4)"), eval("'(2) '(3 4) append"));
        assertEquals(eval("'(1 2 3 4)"), eval("'(1 2) '(3 4) append"));
    }

    @Test
    public void testAppendRecursiveFrame() {
        run("'[a b - r : a null 'b '(a uncons b self cons) if] function append");
        assertEquals(eval("'(3 4)"), eval("'() '(3 4) append"));
        assertEquals(eval("'(2 3 4)"), eval("'(2) '(3 4) append"));
        assertEquals(eval("'(1 2 3 4)"), eval("'(1 2) '(3 4) append"));
    }

    /**
     * <pre><code>
     * def fib(n: int) -> int:
     * if n < 2:
     *     return n
     * else:
     *     return fib(n=n-1) + fib(n=n-2)
     */
    @Test
    public void testFibonacciFrameRecursion() {
        run("'[n - r : n 2 < n '(n 1 - self n 2 - self +) if] function fib");
        assertEquals(i(0), eval("0 fib"));
        assertEquals(i(1), eval("1 fib"));
        assertEquals(i(1), eval("2 fib"));
        assertEquals(i(2), eval("3 fib"));
        assertEquals(i(3), eval("4 fib"));
        assertEquals(i(5), eval("5 fib"));
        assertEquals(i(8), eval("6 fib"));
        assertEquals(i(13), eval("7 fib"));
    }

    /**
     * <pre><code>
     * def fib(n: int) -> int:
     * a, b = 1, 0
     * for _ in range(n):
     *     a, b = b, a+b
     * return b
     * </code><pre>
     */
    @Test
    public void testFibonacciFrameFor() {
        run("'[n - r : 1 variable a 0 variable b 1 n 1 range '(drop a b + b set a set b) for b] function fib");
        assertEquals(i(0), eval("0 fib"));
        assertEquals(i(1), eval("1 fib"));
        assertEquals(i(1), eval("2 fib"));
        assertEquals(i(2), eval("3 fib"));
        assertEquals(i(3), eval("4 fib"));
        assertEquals(i(5), eval("5 fib"));
        assertEquals(i(8), eval("6 fib"));
        assertEquals(i(13), eval("7 fib"));
    }

    @Test
    public void testFilterFrame() {
        run("'[l p - r :"
            + "l null"
            + "    'nil"
            + "    '(l car p call"
            + "        '(l car l cdr p filter stack cons)"
            + "        '(l cdr p filter)"
            + "    if)"
            + "if] function filter");
        // 空リストを返すときは「'nil」または「''()」とする必要がある。
        // 「'()」はNOPとなる。
        // run("'[p l - r : l null ''() '(l car p call '(l car p l cdr filter stack cons) '(p l cdr filter) if) if] stack function filter");
        assertEquals(eval("'(1 2 3)"), eval(" '(1 2 3 4 5 6) '(4 <) filter"));
        assertEquals(eval("'()"), eval("'(4 5 6) '(4 <) filter"));
        assertEquals(eval("'(4 5 6)"), eval("'(1 2 3 4 5 6) '(4 >=) filter"));
    }

    @Test
    public void testFilterReverse() {
        run("'[l p - r : nil l '(dup p call 'rcons 'drop if) for reverse] function filter");
        assertEquals(eval("'(1 2 3)"), eval(" '(1 2 3 4 5 6) '(4 <) filter"));
        assertEquals(eval("'()"), eval("'(4 5 6) '(4 <) filter"));
        assertEquals(eval("'(4 5 6)"), eval("'(1 2 3 4 5 6) '(4 >=) filter"));
    }

    @Test
    public void testMapFrame() {
        run("'[l p - r :"
            + "l null"
            + "    'nil"
            + "    '(l car p call l cdr p self cons)"
            + "if] function map");
        assertEquals(eval("'()"), eval("'() '(1 +) map"));
        assertEquals(eval("'(1 2 3)"), eval(" '(0 1 2) '(1 +) map"));
    }

    @Test
    public void testMapFrameReverse() {
        run("'[l p - r : '() l '(p call rcons) for reverse] function map");
        assertEquals(eval("'()"), eval("'() '(1 +) map"));
        assertEquals(eval("'(1 2 3)"), eval(" '(0 1 2) '(1 +) map"));
    }

    @Test
    public void testMapFrameReverseSet() {
        run("'[l p - r : '() variable m l '(p call m cons set m) for m reverse] function map");
        assertEquals(eval("'()"), eval("'() '(1 +) map"));
        assertEquals(eval("'(1 2 3)"), eval(" '(0 1 2) '(1 +) map"));
    }

}
