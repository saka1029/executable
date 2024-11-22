package saka1029.executable;

public class DefineLocal extends SymbolMacro {

    final int offset;

    DefineLocal(Symbol symbol, int offset) {
        super(symbol);
        this.offset = offset;
    }

    public static DefineLocal of(Symbol symbol, int offset) {
        return new DefineLocal(symbol, offset);
    }

    @Override
    public void execute(Context c) {
        Executable body = c.pop();
        c.stack.set(c.fp + offset, body);
    }

    @Override
    public String toString() {
        // return "= %s@%d".formatted(symbol, offset);
        return "= " + symbol;
    }

}
