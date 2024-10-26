package saka1029.executable;

public class DefinedBody implements Executable {

    public final Executable body;

    DefinedBody(Executable body) {
        this.body = body;
    }

    public static DefinedBody of(Executable body) {
        return new DefinedBody(body);
    }

    @Override
    public void execute(Context c) {
        body.call(c);
    }

    @Override
    public String toString() {
        return body.toString();
    }
}
