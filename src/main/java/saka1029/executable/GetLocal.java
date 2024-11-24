package saka1029.executable;

public class GetLocal extends SymbolMacro {

    final int offset;

    GetLocal(Symbol symbol, int offset) {
        super(symbol);
        this.offset = offset;
    }

    public static GetLocal of(Symbol symbol, int offset) {
        return new GetLocal(symbol, offset);
    }

    @Override
    public void execute(Context c) {
        if (offset == 0)
            c.stack.get(c.fp).execute(c); // self
        else
            c.push(c.stack.get(c.fp + offset));
    }

    @Override
    public String toString() {
        return symbol.toString();
    }
}
