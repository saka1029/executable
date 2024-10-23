package saka1029.executable;

public class Helper {

    private Helper() {
    }

    public Bool b(boolean value) {
        return Bool.of(value);
    }

    public boolean b(Executable e) {
        return ((Bool)e).value;
    }

    public Int i(int value) {
        return Int.of(value);
    }

    public int i(Executable e) {
        return ((Int)e).value;
    }

    public List l(Executable... es) {
        return Cons.of(es);
    }

}
