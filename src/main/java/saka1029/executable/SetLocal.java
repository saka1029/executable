package saka1029.executable;

public class SetLocal extends SymbolMacro {

    final FrameOffset position;

    SetLocal(Symbol symbol, FrameOffset position) {
        super(symbol);
        this.position = position;
    }

    public static SetLocal of(Symbol symbol, FrameOffset position) {
        return new SetLocal(symbol, position);
    }

    @Override
    public void execute(Context c) {
        Executable value = c.pop();
        int fp = c.fp(position.frame);
        c.stack.set(fp + position.offset, value);
    }

    @Override
    public String toString() {
        return "set " + symbol;
    }
}
