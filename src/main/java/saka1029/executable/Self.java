package saka1029.executable;

public class Self implements Executable {

    final Executable body;

    Self(Executable body) {
        this.body = body;
    }

    public static Self of(Executable body) {
        return new Self(body);
    }

    @Override
    public void execute(Context c) {
        body.execute(c);
    }

    @Override
    public String toString() {
        return "self";
    }
}
