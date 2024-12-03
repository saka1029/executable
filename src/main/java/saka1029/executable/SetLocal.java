package saka1029.executable;

public class SetLocal extends SymbolMacro {

    final Frame frame;
    final int offset;

    SetLocal(Symbol symbol, Frame frame, int offset) {
        super(symbol);
        this.frame = frame;
        this.offset = offset;
    }

    public static SetLocal of(Symbol symbol, Frame frame, int offset) {
        return new SetLocal(symbol, frame, offset);
    }

    @Override
    public void execute(Context c) {
        Executable value = c.pop();
        int fp = c.fp(frame);
        c.stack.set(fp + offset, value);
    }

    @Override
    public String toString() {
        return "set " + symbol;
    }

}
