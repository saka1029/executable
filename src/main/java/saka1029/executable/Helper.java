package saka1029.executable;

public class Helper {

    private Helper() {
    }

    public static Bool TRUE = Bool.TRUE;
    public static Bool FALSE = Bool.FALSE;

    static RuntimeException error(String format, Object... args) {
        return new RuntimeException(format.formatted(args));
    }

    public static Bool b(boolean value) {
        return Bool.of(value);
    }

    public static boolean b(Executable e, String at) {
        if (!(e instanceof Bool b))
            throw error("%s: '%s' is not bool", at, e);
        return b.value;
    }

    public static boolean b(Executable e) {
        return b(e, "unknown");
    }

    public static Int i(int value) {
        return Int.of(value);
    }

    public static int asI(Executable e, String at) {
        if (!(e instanceof Int i))
            throw error("%s: '%s' is not int", at, e);
        return i.value;
    }

    public static Symbol sym(String name) {
        return Symbol.of(name);
    }

    public static Comp asComp(Executable e, String at) {
        if (!(e instanceof Comp c))
            throw error("%s: '%s' is not comparable", at, e);
        return c;
    }

    public static Quote quote(Executable e) {
        return Quote.of(e);
    }

    public static List NIL = List.NIL;

    public static List list(Executable... es) {
        return Cons.list(es);
    }

    public static List list(java.util.List<Executable> es) {
        return Cons.list(es);
    }

    public static List asList(Executable e, String at) {
        if (!(e instanceof List list))
            throw error("%s: '%s' is not list", at, e);
        return list;
    }

    public static Cons asCons(Executable e, String at) {
        if (!(e instanceof Cons cons))
            throw error("%s: '%s' is not cons", at, e);
        return cons;
    }

    public static DefineGlobal defun(Symbol symbol) {
        return DefineGlobal.of(symbol, true);
    }

    public static DefineGlobal defvar(Symbol symbol) {
        return DefineGlobal.of(symbol, false);
    }

    public static SetGlobal set(Symbol symbol) {
        return SetGlobal.of(symbol);
    }
}
