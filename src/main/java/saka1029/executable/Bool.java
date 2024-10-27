package saka1029.executable;

public class Bool implements Comp {

    public final boolean value;

    public static final Bool TRUE = new Bool(true);
    public static final Bool FALSE = new Bool(false);

    private Bool(boolean value) {
        this.value = value;
    }

    public static Bool of(boolean value) {
        return value ? TRUE : FALSE;
    }

    @Override
    public int compareTo(Comp o) {
        return Boolean.compare(value, ((Bool)o).value);
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
