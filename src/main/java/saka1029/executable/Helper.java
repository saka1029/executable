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

    public static boolean asBool(Executable e, String at) {
        if (!(e instanceof Bool b))
            throw error("%s: '%s' is not bool", at, e);
        return b.value;
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

    @SuppressWarnings("unchecked")
    public static Iterable<Executable> asIterable(Executable e, String at) {
        if (!(e instanceof Iterable it))
            throw error("%s: '%s' is not iterable", at, e);
        return (Iterable<Executable>)it;
    }

    public static Array asArray(Executable e, String at) {
        if (!(e instanceof Array a))
            throw error("%s: '%s' is not array", at, e);
        return a;
    }

    public static Array array(Executable... es) {
        return Array.of(es);
    }

    public static DefineGlobal defun(Symbol symbol, Executable body) {
        return DefineGlobal.of(symbol, DefineType.FUNCTION, body);
    }

    public static DefineGlobal defvar(Symbol symbol, Executable value) {
        return DefineGlobal.of(symbol, DefineType.VARIABLE, value);
    }

    public static SetGlobal set(Symbol symbol) {
        return SetGlobal.of(symbol);
    }
}
