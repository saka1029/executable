package saka1029.executable;

import static org.junit.Assert.assertEquals;
import static saka1029.executable.Helper.*;

import org.junit.Test;

public class TestAlgorithm {

    Parser p = new Parser();

    void run(Context c, String s) {
        c.run(p.parse(s));
    }

    Executable eval(Context c, String s) {
        return c.eval(p.parse(s));
    }

    @Test
    public void testReverseFor() {
        Context c = Context.of();
        run(c, "'('() swap 'rcons for) function reverse");
        assertEquals(eval(c, "'()"), eval(c, "'() reverse"));
        assertEquals(eval(c, "'(1)"), eval(c, "'(1) reverse"));
        assertEquals(eval(c, "'(3 2 1)"), eval(c, "'(1 2 3) reverse"));
        assertEquals(eval(c, "'(3 (20 21) 1)"), eval(c, "'(1 (20 21) 3) reverse"));
    }

    @Test
    public void testReverseForFrame() {
        Context c = Context.of();
        run(c, "'[i - r : '() variable a i '(a cons set a) for a] function reverse");
        assertEquals(eval(c, "'()"), eval(c, "'() reverse"));
        assertEquals(eval(c, "'(1)"), eval(c, "'(1) reverse"));
        assertEquals(eval(c, "'(3 2 1)"), eval(c, "'(1 2 3) reverse"));
        assertEquals(eval(c, "'(3 (20 21) 1)"), eval(c, "'(1 (20 21) 3) reverse"));
    }

    @Test
    public void testReverseFrameNest() {
        Context c = Context.of();
        run(c, "'[i - r : '[i a - r : i null 'a '(i cdr i car a cons rev) if] function rev i '() rev] function reverse");
        // run(c, "'[i - r : '[i a - r : i null 'a '(i uncons swap a cons rev) if] function rev i '() rev] function reverse");
        assertEquals(eval(c, "'()"), eval(c, "'() reverse"));
        assertEquals(eval(c, "'(1)"), eval(c, "'(1) reverse"));
        assertEquals(eval(c, "'(3 2 1)"), eval(c, "'(1 2 3) reverse"));
        assertEquals(eval(c, "'(3 (20 21) 1)"), eval(c, "'(1 (20 21) 3) reverse"));
    }

    @Test
    public void testReverseFrameNestDirect() {
        Context c = Context.of();
        run(c, "'[i - r : i '() [i a - r : i null 'a '(i cdr i car a cons self) if]] function reverse");
        assertEquals(eval(c, "'()"), eval(c, "'() reverse"));
        assertEquals(eval(c, "'(1)"), eval(c, "'(1) reverse"));
        assertEquals(eval(c, "'(3 2 1)"), eval(c, "'(1 2 3) reverse"));
        assertEquals(eval(c, "'(3 (20 21) 1)"), eval(c, "'(1 (20 21) 3) reverse"));
    }

    @Test
    public void testReverseFrameWhile() {
        Context c = Context.of();
        run(c, "'[i - r : '() '(i null not) '(i uncons set i rcons) while] function reverse");
        assertEquals(eval(c, "'()"), eval(c, "'() reverse"));
        assertEquals(eval(c, "'(1)"), eval(c, "'(1) reverse"));
        assertEquals(eval(c, "'(3 2 1)"), eval(c, "'(1 2 3) reverse"));
        assertEquals(eval(c, "'(3 (20 21) 1)"), eval(c, "'(1 (20 21) 3) reverse"));
    }

    @Test
    public void testLength() {
        Context c = Context.of();
        run(c, "'(0 swap '(drop 1 +) for) function length");
        assertEquals(eval(c, "0"), eval(c, "'() length"));
        assertEquals(eval(c, "1"), eval(c, "'(2) length"));
        assertEquals(eval(c, "2"), eval(c, "'(1 2) length"));
    }

    @Test
    public void testLengthRecursive() {
        Context c = Context.of();
        run(c, "'(dup null 0 '(cdr length 1 +) if) function length");
        assertEquals(eval(c, "0"), eval(c, "'() length"));
        assertEquals(eval(c, "1"), eval(c, "'(2) length"));
        assertEquals(eval(c, "2"), eval(c, "'(1 2) length"));
    }

    @Test
    public void testLengthFrameFor() {
        Context c = Context.of();
        run(c, "'[i - r : 0 variable a i '(drop a 1 + set a) for a] function length");
        assertEquals(eval(c, "0"), eval(c, "'() length"));
        assertEquals(eval(c, "1"), eval(c, "'(2) length"));
        assertEquals(eval(c, "2"), eval(c, "'(1 2) length"));
    }

    @Test
    public void testLengthFrameRecursive() {
        Context c = Context.of();
        run(c, "'[i - r : i null 0 '(i cdr self 1 +) if] function length");
        assertEquals(eval(c, "0"), eval(c, "'() length"));
        assertEquals(eval(c, "1"), eval(c, "'(2) length"));
        assertEquals(eval(c, "2"), eval(c, "'(1 2) length"));
    }

    @Test
    public void testAppendRecursive() {
        Context c = Context.of();
        run(c, "'(swap dup null 'drop '(uncons rot append cons) if) function append");
        assertEquals(eval(c, "'(3 4)"), eval(c, "'() '(3 4) append"));
        assertEquals(eval(c, "'(2 3 4)"), eval(c, "'(2) '(3 4) append"));
        assertEquals(eval(c, "'(1 2 3 4)"), eval(c, "'(1 2) '(3 4) append"));
    }

    @Test
    public void testAppendRecursiveFrame() {
        Context c = Context.of();
        run(c, "'[a b - r : a null 'b '(a uncons b self cons) if] function append");
        assertEquals(eval(c, "'(3 4)"), eval(c, "'() '(3 4) append"));
        assertEquals(eval(c, "'(2 3 4)"), eval(c, "'(2) '(3 4) append"));
        assertEquals(eval(c, "'(1 2 3 4)"), eval(c, "'(1 2) '(3 4) append"));
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
        Context c = Context.of();
        run(c, "'[n - r : n 2 < n '(n 1 - self n 2 - self +) if] function fib");
        assertEquals(i(0), eval(c, "0 fib"));
        assertEquals(i(1), eval(c, "1 fib"));
        assertEquals(i(1), eval(c, "2 fib"));
        assertEquals(i(2), eval(c, "3 fib"));
        assertEquals(i(3), eval(c, "4 fib"));
        assertEquals(i(5), eval(c, "5 fib"));
        assertEquals(i(8), eval(c, "6 fib"));
        assertEquals(i(13), eval(c, "7 fib"));
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
        Context c = Context.of();
        run(c, "'[n - r : 1 variable a 0 variable b 1 n 1 range '(drop a b + b set a set b) for b] function fib");
        assertEquals(i(0), eval(c, "0 fib"));
        assertEquals(i(1), eval(c, "1 fib"));
        assertEquals(i(1), eval(c, "2 fib"));
        assertEquals(i(2), eval(c, "3 fib"));
        assertEquals(i(3), eval(c, "4 fib"));
        assertEquals(i(5), eval(c, "5 fib"));
        assertEquals(i(8), eval(c, "6 fib"));
        assertEquals(i(13), eval(c, "7 fib"));
    }

    @Test
    public void testFilterBuiltin() {
        Context c = Context.of();
        assertEquals(eval(c, "'(1 2 3)"), eval(c, " '(1 2 3 4 5 6) '(4 <) filter"));
        assertEquals(eval(c, "'()"), eval(c, "'(4 5 6) '(4 <) filter"));
        assertEquals(eval(c, "'(4 5 6)"), eval(c, "'(1 2 3 4 5 6) '(4 >=) filter"));
        assertEquals(eval(c, "'(2 4 6)"), eval(c, "'(1 2 3 4 5 6) '(2 % 0 ==) filter"));
    }

    @Test
    public void testFilterFrame() {
        Context c = Context.of();
        run(c, "'[l p - r :"
            + "l null"
            + "    'nil"
            + "    '(l car p call"
            + "        '(l car l cdr p filter cons)"
            + "        '(l cdr p filter)"
            + "    if)"
            + "if] function filter");
        // 空リストを返すときは「'nil」または「''()」とする必要がある。
        // 「'()」はNOPとなる。
        // run(c, "'[p l - r : l null ''() '(l car p call '(l car p l cdr filter cons) '(p l cdr filter) if) if] function filter");
        assertEquals(eval(c, "'(1 2 3)"), eval(c, " '(1 2 3 4 5 6) '(4 <) filter"));
        assertEquals(eval(c, "'()"), eval(c, "'(4 5 6) '(4 <) filter"));
        assertEquals(eval(c, "'(4 5 6)"), eval(c, "'(1 2 3 4 5 6) '(4 >=) filter"));
        assertEquals(eval(c, "'(2 4 6)"), eval(c, "'(1 2 3 4 5 6) '(2 % 0 ==) filter"));
    }

    @Test
    public void testFilterReverse() {
        Context c = Context.of();
        run(c, "'[l p - r : nil l '(dup p call 'rcons 'drop if) for reverse] function filter");
        assertEquals(eval(c, "'(1 2 3)"), eval(c, " '(1 2 3 4 5 6) '(4 <) filter"));
        assertEquals(eval(c, "'()"), eval(c, "'(4 5 6) '(4 <) filter"));
        assertEquals(eval(c, "'(4 5 6)"), eval(c, "'(1 2 3 4 5 6) '(4 >=) filter"));
    }

    @Test
    public void testMapBuiltin() {
        Context c = Context.of();
        assertEquals(eval(c, "'()"), eval(c, "'() '(1 +) map"));
        assertEquals(eval(c, "'(1 2 3)"), eval(c, " '(0 1 2) '(1 +) map"));
        assertEquals(eval(c, "'(1 4 9)"), eval(c, " '(1 2 3) '(dup *) map"));
    }

    @Test
    public void testMapFrame() {
        Context c = Context.of();
        run(c, "'[l p - r :"
            + "l null"
            + "    'nil"
            + "    '(l car p call l cdr p self cons)"
            + "if] function map");
        assertEquals(eval(c, "'()"), eval(c, "'() '(1 +) map"));
        assertEquals(eval(c, "'(1 2 3)"), eval(c, " '(0 1 2) '(1 +) map"));
        assertEquals(eval(c, "'(1 4 9)"), eval(c, " '(1 2 3) '(dup *) map"));
    }

    @Test
    public void testMapFrameReverse() {
        Context c = Context.of();
        run(c, "'[l p - r : '() l '(p call rcons) for reverse] function map");
        assertEquals(eval(c, "'()"), eval(c, "'() '(1 +) map"));
        assertEquals(eval(c, "'(1 2 3)"), eval(c, " '(0 1 2) '(1 +) map"));
        assertEquals(eval(c, "'(1 4 9)"), eval(c, " '(1 2 3) '(dup *) map"));
    }

    @Test
    public void testMapFrameReverseSet() {
        Context c = Context.of();
        run(c, "'[l p - r : '() variable m l '(p call m cons set m) for m reverse] function map");
        assertEquals(eval(c, "'()"), eval(c, "'() '(1 +) map"));
        assertEquals(eval(c, "'(1 2 3)"), eval(c, " '(0 1 2) '(1 +) map"));
        assertEquals(eval(c, "'(1 4 9)"), eval(c, " '(1 2 3) '(dup *) map"));
    }

    /**
     * 与えられたリストの先頭をピボットとし、
     * 残りのリストからピボット以上のリストと以下のリストに振り分ける。
     * それぞれのリストをソート後にマージする。
     * リスト振り分け時にconsを使用しているため、振り分け結果は逆順になる。
     * したがってソート結果は安定的(stable)ではない。
     */
    @Test
    public void testSortFrame() {
        Context c = Context.of();
        run(c, "'[list p - r : "
            + "    list null "
            + "    'nil"
            + "    '("
            + "        list uncons variable rest variable pivot "
            + "        nil variable left nil variable right "
            + "               rest "
            + "               '( "
            + "                        dup pivot p call "
            + "                        '(left cons set left) "
            + "                        '(right cons set right) "
            + "                    if "
            + "                ) "
            + "            for "
            + "            left p sort pivot right p sort cons append "
            + "    ) "
            + "if] function sort");
        assertEquals(eval(c, "'()"), eval(c, "'() '< sort"));
        assertEquals(eval(c, "'(1 2 3 4 5)"), eval(c, "'(1 5 3 4 2) '< sort"));
        assertEquals(eval(c, "'(5 4 3 2 1)"), eval(c, "'(1 5 3 4 2) '> sort"));
    }

    /**
     * 与えられたリストの先頭をピボットとし、
     * 残りのリストからピボット以上のリストと以下のリストを作成し、
     * それぞれのリストをソート後にマージする。
     */
    @Test
    public void testSortFrameByFilter() {
        Context c = Context.of();
        run(c, "'[list predicate - r : "
            + "    list null "
            + "    'nil "
            + "    '("
            + "        list uncons variable rest variable pivot "
            + "        rest '(pivot predicate call) filter predicate sort "
            + "        pivot "
            + "        rest '(pivot predicate call not) filter predicate sort "
            + "        cons append "
            + "    ) "
            + "if] function sort");
        assertEquals(eval(c, "'()"), eval(c, "'() '< sort"));
        assertEquals(eval(c, "'(1 2 3 4 5)"), eval(c, "'(1 5 3 4 2) '< sort"));
        assertEquals(eval(c, "'(5 4 3 2 1)"), eval(c, "'(1 5 3 4 2) '> sort"));
    }

    @Test
    public void testCompose() {
        Context c = Context.of();
        assertEquals(i(3), eval(c, "1 2 '+ nil cons cons cons call"));
        assertEquals(i(3), eval(c, "1 2 '(+) nil cons cons cons call"));
        assertEquals(i(3), eval(c, "1 2 '+ call"));
        run(c, "3 variable THREE");
        assertEquals(i(4), eval(c, "'(1 THREE +) dup print call"));
    }
}
