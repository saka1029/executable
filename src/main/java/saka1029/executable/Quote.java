package saka1029.executable;

public class Quote implements Executable {

    public final Executable value;

    Quote(Executable body) {
        this.value = body;
    }

    public static Quote of(Executable value) {
        return new Quote(value);
    }

    @Override
    public void execute(Context c) {
        c.push(value);
    }

    @Override
    public String toString() {
        return "'" + value;
    }
}
