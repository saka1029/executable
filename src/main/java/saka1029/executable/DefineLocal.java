package saka1029.executable;

public class DefineLocal extends SymbolMacro {

    final int offset;
    final Frame frame;
    final Executable value;

    DefineLocal(Symbol symbol, Frame frame, int offset, Executable value) {
        super(symbol);
        this.frame = frame;
        this.offset = offset;
        this.value = value;
    }

    public static DefineLocal of(Symbol symbol, Frame frame, int offset, Executable value) {
        return new DefineLocal(symbol, frame, offset, value);
    }

    @Override
    public void execute(Context context) {
        Executable epilog = c -> {
            Executable body = c.pop();
            int fp = c.fp(frame);
            c.stack.set(fp + offset, body);
        };
        context.executables.addLast(java.util.List.of(value, epilog).iterator());
    }

    @Override
    public String toString() {
        return "define %s@%d".formatted(symbol, offset);
    }
}
