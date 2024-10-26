package saka1029.executable;

public class Helper {

    private Helper() {
    }

    public static Bool TRUE = Bool.TRUE;
    public static Bool FALSE = Bool.FALSE;

    public static Bool b(boolean value) {
        return Bool.of(value);
    }

    public static boolean b(Executable e) {
        return ((Bool)e).value;
    }

    public static Int i(int value) {
        return Int.of(value);
    }

    public static int i(Executable e) {
        return ((Int)e).value;
    }

    public static Symbol s(String name) {
        return Symbol.of(name);
    }

    public static List NIL = List.NIL;

    public static List l(Executable... es) {
        return List.of(es);
    }

    public static Define define(Symbol symbol) {
        return Define.of(symbol);
    }
}
