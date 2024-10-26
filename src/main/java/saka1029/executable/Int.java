package saka1029.executable;

public class Int implements Comp {

    public final int value;

    private Int(int value) {
        this.value = value;
    }

    public static Int of(int value) {
        return new Int(value);
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Int i && i.value == value;
    }

    @Override
    public int compareTo(Executable o) {
        return Integer.compare(value, ((Int)o).value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
