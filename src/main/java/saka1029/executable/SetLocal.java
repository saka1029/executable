package saka1029.executable;

public class SetLocal extends SymbolMacro {

    final int offset;

    SetLocal(Symbol symbol, int offset) {
        super(symbol);
        this.offset = offset;
    }

    public static SetLocal of(Symbol symbol, int offset) {
        return new SetLocal(symbol, offset);
    }

    @Override
    public void execute(Context c) {
        Executable value = c.pop();
        c.stack.set(c.fp + offset, value);
    }

    @Override
    public String toString() {
        // return "! %s@%d".formatted(symbol, offset);
        return "set " + symbol;
    }

}
