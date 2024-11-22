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
        c.stack.get(c.fp + offset).execute(c);
    }

    @Override
    public String toString() {
        // return "%s@%d".formatted(symbol, offset);
        return symbol.toString();
    }

}
