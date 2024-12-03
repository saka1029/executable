package saka1029.executable;

public class DefineLocal extends SymbolMacro {

    final int offset;
    final Frame frame;

    DefineLocal(Symbol symbol, Frame frame, int offset) {
        super(symbol);
        this.frame = frame;
        this.offset = offset;
    }

    public static DefineLocal of(Symbol symbol, Frame frame, int offset) {
        return new DefineLocal(symbol, frame, offset);
    }

    @Override
    public void execute(Context c) {
        Executable body = c.pop();
        int fp = c.fp(frame);
        c.stack.set(fp + offset, body);
    }

    @Override
    public String toString() {
        return "define %s@%d".formatted(symbol, offset);
    }

}
