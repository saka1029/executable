package saka1029.executable;

public class DefineLocal extends SymbolMacro {

    final int offset;
    final Frame frame;
    final DefineType type;
    final Executable value;

    DefineLocal(Symbol symbol, Frame frame, int offset, DefineType type, Executable value) {
        super(symbol);
        this.frame = frame;
        this.offset = offset;
        this.type = type;
        this.value = value;
    }

    public static DefineLocal of(Symbol symbol, Frame frame, int offset, DefineType type, Executable value) {
        return new DefineLocal(symbol, frame, offset, type, value);
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
        return "%s %s@%d %s".formatted(type, symbol, offset, value);
    }
}
