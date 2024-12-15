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
        run(c, "function reverse '('() swap 'rcons for)");
        assertEquals(eval(c, "'()"), eval(c, "'() reverse"));
        assertEquals(eval(c, "'(1)"), eval(c, "'(1) reverse"));
        assertEquals(eval(c, "'(3 2 1)"), eval(c, "'(1 2 3) reverse"));
        assertEquals(eval(c, "'(3 (20 21) 1)"), eval(c, "'(1 (20 21) 3) reverse"));
    }

    @Test
    public void testReverseForFrame() {
        Context c = Context.of();
        run(c, "function reverse '[i - r : variable a '() i '(a cons set a) for a]");
        assertEquals(eval(c, "'()"), eval(c, "'() reverse"));
        assertEquals(eval(c, "'(1)"), eval(c, "'(1) reverse"));
        assertEquals(eval(c, "'(3 2 1)"), eval(c, "'(1 2 3) reverse"));
        assertEquals(eval(c, "'(3 (20 21) 1)"), eval(c, "'(1 (20 21) 3) reverse"));
    }

    @Test
    public void testReverseFrameNest() {
        Context c = Context.of();
        run(c, "function reverse '[i - r : function rev '[i a - r : i null 'a '(i cdr i car a cons rev) if] i '() rev]");
        // run(c, "function reverse '[i - r : function rev '[i a - r : i null 'a '(i cdr i car a cons self) if] i '() rev]");
        // run(c, "'[i - r : '[i a - r : i null 'a '(i uncons swap a cons rev) if] function rev i '() rev] function reverse");
        assertEquals(eval(c, "'()"), eval(c, "'() reverse"));
        assertEquals(eval(c, "'(1)"), eval(c, "'(1) reverse"));
        assertEquals(eval(c, "'(3 2 1)"), eval(c, "'(1 2 3) reverse"));
        assertEquals(eval(c, "'(3 (20 21) 1)"), eval(c, "'(1 (20 21) 3) reverse"));
    }

    @Test
    public void testReverseFrameNestDirect() {
        Context c = Context.of();
        run(c, "function reverse '[i - r : i '() [i a - r : i null 'a '(i cdr i car a cons self) if]]");
        assertEquals(eval(c, "'()"), eval(c, "'() reverse"));
        assertEquals(eval(c, "'(1)"), eval(c, "'(1) reverse"));
        assertEquals(eval(c, "'(3 2 1)"), eval(c, "'(1 2 3) reverse"));
        assertEquals(eval(c, "'(3 (20 21) 1)"), eval(c, "'(1 (20 21) 3) reverse"));
    }

    @Test
    public void testReverseFrameWhile() {
        Context c = Context.of();
        run(c, "function reverse '[i - r : '() '(i null not) '(i uncons set i rcons) while]");
        assertEquals(eval(c, "'()"), eval(c, "'() reverse"));
        assertEquals(eval(c, "'(1)"), eval(c, "'(1) reverse"));
        assertEquals(eval(c, "'(3 2 1)"), eval(c, "'(1 2 3) reverse"));
        assertEquals(eval(c, "'(3 (20 21) 1)"), eval(c, "'(1 (20 21) 3) reverse"));
    }

    @Test
    public void testLength() {
        Context c = Context.of();
        run(c, "function length '(0 swap '(drop 1 +) for)");
        assertEquals(eval(c, "0"), eval(c, "'() length"));
        assertEquals(eval(c, "1"), eval(c, "'(2) length"));
        assertEquals(eval(c, "2"), eval(c, "'(1 2) length"));
    }

    @Test
    public void testLengthRecursive() {
        Context c = Context.of();
        run(c, "function length '(dup null 0 '(cdr length 1 +) if)");
        assertEquals(eval(c, "0"), eval(c, "'() length"));
        assertEquals(eval(c, "1"), eval(c, "'(2) length"));
        assertEquals(eval(c, "2"), eval(c, "'(1 2) length"));
    }

    @Test
    public void testLengthFrameFor() {
        Context c = Context.of();
        run(c, "function length '[i - r : variable a 0 i '(drop a 1 + set a) for a]");
        assertEquals(eval(c, "0"), eval(c, "'() length"));
        assertEquals(eval(c, "1"), eval(c, "'(2) length"));
        assertEquals(eval(c, "2"), eval(c, "'(1 2) length"));
    }

    @Test
    public void testLengthFrameRecursive() {
        Context c = Context.of();
        run(c, "function length '[i - r : i null 0 '(i cdr self 1 +) if]");
        assertEquals(eval(c, "0"), eval(c, "'() length"));
        assertEquals(eval(c, "1"), eval(c, "'(2) length"));
        assertEquals(eval(c, "2"), eval(c, "'(1 2) length"));
    }

    @Test
    public void testAppendRecursive() {
        Context c = Context.of();
        run(c, "function append '(swap dup null 'drop '(uncons rot append cons) if)");
        assertEquals(eval(c, "'(3 4)"), eval(c, "'() '(3 4) append"));
        assertEquals(eval(c, "'(2 3 4)"), eval(c, "'(2) '(3 4) append"));
        assertEquals(eval(c, "'(1 2 3 4)"), eval(c, "'(1 2) '(3 4) append"));
    }

    @Test
    public void testAppendRecursiveFrame() {
        Context c = Context.of();
        run(c, "function append '[a b - r : a null 'b '(a uncons b self cons) if]");
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
        run(c, "function fib '[n - r : n 2 < n '(n 1 - self n 2 - self +) if]");
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
        run(c, "function fib '[n - r : variable a 1 variable b 0 1 n 1 range '(drop a b + b set a set b) for b]");
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
        run(c, """
            function filter '[l p - r :
            l null
            'nil
            '(l car p call
                '(l car l cdr p filter cons)
                '(l cdr p filter)
                if)
            if]""");
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
        run(c, "function filter '[l p - r : nil l '(dup p call 'rcons 'drop if) for reverse]");
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
        run(c, "function map '[l p - r :"
            + "l null"
            + "    'nil"
            + "    '(l car p call l cdr p self cons)"
            + "if]");
        assertEquals(eval(c, "'()"), eval(c, "'() '(1 +) map"));
        assertEquals(eval(c, "'(1 2 3)"), eval(c, " '(0 1 2) '(1 +) map"));
        assertEquals(eval(c, "'(1 4 9)"), eval(c, " '(1 2 3) '(dup *) map"));
    }

    @Test
    public void testMapFrameReverse() {
        Context c = Context.of();
        run(c, "function map '[l p - r : '() l '(p call rcons) for reverse]");
        assertEquals(eval(c, "'()"), eval(c, "'() '(1 +) map"));
        assertEquals(eval(c, "'(1 2 3)"), eval(c, " '(0 1 2) '(1 +) map"));
        assertEquals(eval(c, "'(1 4 9)"), eval(c, " '(1 2 3) '(dup *) map"));
    }

    @Test
    public void testMapFrameReverseSet() {
        Context c = Context.of();
        run(c, "function map '[l p - r : variable m '() l '(p call m cons set m) for m reverse]");
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
        run(c, """
            function sort '[list predicate - r :
                list null
                'nil
                '(
                    variable pivot (list car)
                    variable rest (list cdr)
                    variable left nil
                    variable right nil
                       rest
                       '(
                                dup pivot predicate call
                                '(left cons set left)
                                '(right cons set right)
                            if
                        )
                    for
                    (left predicate sort) pivot (right predicate sort) cons append
                )
            if]""");
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
        run(c, """
            function sort '[list predicate - r :
                list null
                'nil
                '(
                    variable pivot (list car)
                    variable rest (list cdr)
                    rest '(pivot predicate call) filter predicate sort
                    pivot
                    rest '(pivot predicate call not) filter predicate sort
                    cons append
                )
                if
                ]""");
        assertEquals(eval(c, "'()"), eval(c, "'() '< sort"));
        assertEquals(eval(c, "'(1 2 3 4 5)"), eval(c, "'(1 5 3 4 2) '< sort"));
        assertEquals(eval(c, "'(5 4 3 2 1)"), eval(c, "'(1 5 3 4 2) '> sort"));
    }

    @Test
    public void testSortFrameByFilterNoLocalVariable() {
        Context c = Context.of();
        run(c, """
            function sort '[list predicate - r :
                list null
                'nil
                '(
                    list cdr '(list car predicate call) filter predicate sort
                    list car
                    list cdr '(list car predicate call not) filter predicate sort
                    cons append
                )
                if]""");
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
        run(c, "variable THREE 3");
        assertEquals(i(4), eval(c, "'(1 THREE +) dup print call"));
    }

    @Test
    public void testSieve() {
        Context c = Context.of();
        run(c, """
            function sieve '[ex a - :
                ex dup + a size ex range
                '(false swap a put)
                for]""");
        //                 1     2     3     4      5     6     7
        assertEquals(array(TRUE, TRUE, TRUE, FALSE, TRUE, FALSE, TRUE),
            eval(c, "true 7 array dup 2 swap sieve"));
    }

    @Test
    public void testPrimes() {
        Context c = Context.of();
        // エラトステネスの篩
        run(c, """
            function sieve '[ex a - :
                ex dup + a size ex range
                '(false swap a put)
                for]""");
        run(c, """
            function primes '[max - r :
                variable a (true max array)
                false 1 a put 
                2 a sieve
                3 a size 2 range '(a sieve) for
                a]""");
        // bool配列から素数のリストに変換する。
        run(c, """
            function select '[a - r :
                variable ps nil
                1 a size 1 range '(dup a get '(ps cons set ps) 'drop if) for
                ps reverse]""");
        assertEquals(list(i(2), i(3), i(5), i(7)), eval(c, "10 primes select"));
    }

    static int queen(int n) {
        return new Object() {
            int[] rows = new int[n];
            boolean[] cols = new boolean[n], up = new boolean[2 * n - 1], down = new boolean[2 * n - 1];
            int count = 0;

            void set(int r, int c, boolean b) {
                cols[c] = b;
                up[r - c + n - 1] = b;
                down[r + c] = b;
            }

            int solve(int r) {
                if (r >= n)
                    ++count;
                else
                    for (int c = 0; c < n; ++c)
                        if (!(cols[c] || up[r - c + n - 1] || down[r + c])) {
                            set(r, c, true);
                            rows[r] = c;
                            solve(r + 1);
                            set(r, c, false);
                        }
                return count;
            }
        }.solve(0);
    }

    @Test
    public void testNQueensJava() {
        assertEquals(1, queen(1));
        assertEquals(0, queen(2));
        assertEquals(0, queen(3));
        assertEquals(2, queen(4));
        assertEquals(10, queen(5));
        assertEquals(4, queen(6));
        assertEquals(40, queen(7));
        assertEquals(92, queen(8));
    }

    @Test
    public void testNQueensFrame() {
        Context c = Context.of();
        run(c, """
        function queen '[n - ct :
            variable rows (0 n array)
            variable cols (false n array)
            variable up (false 2 n * 1 - array)
            variable down (false 2 n * 1 - array)
            variable count 0
            function used '[r c b - :
                b c cols put
                b r c - n + up put
                b r c + 1 - down put
            ]
            function found '(count 1 + set count)
            function solve '[r - ct :
                variable c 0
                r n >
                'found
                '(
                    1 n 1 range
                    '(
                        set c
                        c cols get r c - n + up get r c + 1 - down get or or not
                        '(
                            r c true used
                            c r rows put
                            r 1 + solve
                            r c false used
                        )
                        '()
                        if 
                    )
                    for
                )
                if 
                count
            ]
            1 solve
        ]""");
        assertEquals(eval(c, "1"), eval(c, "1 queen"));
        assertEquals(eval(c, "0"), eval(c, "2 queen"));
        assertEquals(eval(c, "0"), eval(c, "3 queen"));
        assertEquals(eval(c, "2"), eval(c, "4 queen"));
        assertEquals(eval(c, "10"), eval(c, "5 queen"));
        assertEquals(eval(c, "4"), eval(c, "6 queen"));
        assertEquals(eval(c, "40"), eval(c, "7 queen"));
        assertEquals(eval(c, "92"), eval(c, "8 queen"));
    }
}
