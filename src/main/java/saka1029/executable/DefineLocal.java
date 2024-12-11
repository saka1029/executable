package saka1029.executable;

public class DefineLocal extends SymbolMacro {

    final FrameOffset offset;
    final Executable value;

    DefineLocal(Symbol symbol, FrameOffset offset, Executable value) {
        super(symbol);
        this.offset = offset;
        this.value = value;
    }

    public static DefineLocal of(Symbol symbol, FrameOffset offset, Executable value) {
        return new DefineLocal(symbol, offset, value);
    }

    @Override
    public void execute(Context context) {
        Executable epilog = c -> {
            Executable body = c.pop();
            int fp = c.fp(offset.frame);
            c.stack.set(fp + offset.offset, body);
        };
        context.execute(value, epilog);
    }

    @Override
    public String toString() {
        return "%s %s@%d %s".formatted(offset.type, symbol, offset.offset, value);
    }
}
