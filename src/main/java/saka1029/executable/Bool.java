package saka1029.executable;

public class Bool implements Value {

    public final boolean value;

    public static final Bool TRUE = new Bool(true);
    public static final Bool FALSE = new Bool(true);

    private Bool(boolean value) {
        this.value = value;
    }

    public static Bool of(boolean value) {
        return value ? TRUE : FALSE;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
