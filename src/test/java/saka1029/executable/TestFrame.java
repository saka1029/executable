package saka1029.executable;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static saka1029.executable.Helper.*;

import java.util.Iterator;

public class TestFrame {

    Parser p = new Parser();
    Context c = Context.of();

    void run(String s) {
        c.run(p.parse(s));
    }

    Executable eval(String s) {
        return c.eval(p.parse(s));
    }

    @Test
    public void testFrameIterator() {
        java.util.List<String> list = java.util.List.of("1", "2", "3");
        Iterator<String> iterator = new Iterator<>() {
            int index = 0, size = list.size() + 2;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public String next() {
                return ++index == 1 ? "A"
                    : index == size ? "Z"
                    : list.get(index - 2);
            }
        };
        assertEquals("A", iterator.next());
        assertEquals("1", iterator.next());
        assertEquals("2", iterator.next());
        assertEquals("3", iterator.next());
        assertEquals("Z", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testArguments() {
        assertEquals(i(3), eval("function plus '[a b . r : a b +] 1 2 plus"));
    }

    @Test
    public void testCall() {
        assertEquals(i(3), eval("1 2 [a b . r : a b +]"));
    }

    @Test
    public void testCallCall() {
        assertEquals(i(3), eval("1 2 '[a b . r : a b +] call"));
    }

    @Test
    public void testLocalFunction() {
        assertEquals(i(25), eval("""
            function hypot '[a b . r :
                function double '(dup *)
                a double b double +
            ]
            3 4 hypot"""));
    }

    @Test
    public void testLocalFrame() {
        assertEquals(i(25), eval("""
            function hypot '[a b . r :
                function double '[a . r : a a *]
                a double b double +
            ]
            3 4 hypot"""));
    }

    @Test
    public void testNoArgumentOneReturn() {
        assertEquals(i(123), eval("function value '[ . r : 123] value"));
    }

    @Test
    public void testNoArgumentTwoReturn() {
        assertEquals(i(579), eval("function value '[ . r s : 123 456] value +"));
    }

    @Test
    public void testTwoArgumentTwoReturn() {
        assertEquals(i(5), eval("function div '[a b . r s : a b / a b %] 11 3 div +"));
    }

    @Test
    public void testSetLocal() {
        assertEquals(i(10), eval("""
            function sum '[list . r :
                variable sum 0
                list '(sum + set sum) for sum]
            '(1 2 3 4) sum
            """));
    }

    @Test
    public void testSetLocalReverse() {
        assertEquals(list(), eval("'()"));
        assertEquals(list(i(4), i(3), i(2), i(1)),
            eval("""
                function reverse '[list . r :
                    variable acc '()
                    list '(acc cons set acc) for acc
                ]
                '(1 2 3 4) reverse"""));
    }

    @Test
    public void testSelf() {
        assertEquals(i(120 ), eval("""
            5 [n . r :
                n 0 <= 1 '(n 1 - self n *) if
            ]"""));
    }

    @Test
    public void testRecursion() {
        run("function fact '[n . r : n 0 <= 1 '(n 1 - fact n *) if]");
        assertEquals(i(1), eval("0 fact"));
        assertEquals(i(1), eval("1 fact"));
        assertEquals(i(2), eval("2 fact"));
        assertEquals(i(6), eval("3 fact"));
        assertEquals(i(24), eval("4 fact"));
        assertEquals(i(120), eval("5 fact"));
    }

    @Test
    public void testCallArgument() {
        run("function compare '[a b c . r : a b c call]");
        assertEquals(eval("0 1 <"), eval("0 1 '< compare"));
        assertEquals(eval("1 0 <"), eval("1 0 '< compare"));
        assertEquals(eval("1 0 >"), eval("1 0 '> compare"));
    }

    @Test
    public void testNestedFunction() {
        assertEquals(eval("'(11 12 21 22 31 32)"), eval("""
            function f1 '[a1 . r : 
                variable v1 12
                function f2 '[a2 . r :
                    variable v2 22
                    function f3 '[a3 . r :
                        variable v3 32
                        a1 v1 a2 v2 a3 v3 nil
                        cons cons cons cons cons cons
                    ]
                    31 f3
                ]
                21 f2
            ]
            11 f1
            """));
    }

    @Test
    public void testNestedFrame() {
        assertEquals(eval("'(11 12 21 22 31 32)"), eval("""
            11 [a1 . r : 
                variable v1 12
                21 [a2 . r :
                    variable v2 22
                    31 [a3 . r :
                        variable v3 32
                        a1 v1 a2 v2 a3 v3 nil
                        cons cons cons cons cons cons
                    ]
                ]
            ]
            """));
    }

    @Test
    public void testSetParentVariable() {
        assertEquals(eval("99"), eval("""
            [.r: 
                variable parent-variable 0
                function child '[.:
                    function grand-child '[.:
                        99 set parent-variable
                    ]
                    grand-child
                ]
                child
                parent-variable 
            ]
            """));
    }

    @Test
    public void testSetParentFunction() {
        assertEquals(eval("99"), eval("""
            [.r:
                function parent-function '[.r: 0]
                function child '[.:
                    function grand-child '[.:
                        '[.r: 99] set parent-function
                    ]
                    grand-child
                ]
                child
                parent-function 
            ]
            """));
    }
}
