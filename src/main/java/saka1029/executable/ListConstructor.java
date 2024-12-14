package saka1029.executable;

public class ListConstructor implements Executable {

    final Executable expressions;

    ListConstructor(Executable expressions) {
        this.expressions = expressions;
    }

    public static ListConstructor of(Executable expressions) {
        return new ListConstructor(expressions);
    }

    @Override
    public void execute(Context context) {
        int start = context.stack.size();
        Executable epilog = c -> {
            List result = List.NIL;
            while (c.stack.size() > start)
                result = Cons.of(c.pop(), result);
            c.push(result);
        };
        context.execute(expressions, epilog);
    }

    @Override
    public String toString() {
        return "`" + expressions;
    }
}
