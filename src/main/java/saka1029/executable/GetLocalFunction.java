package saka1029.executable;

public class GetLocalFunction extends SymbolMacro {

    final int offset;

    GetLocalFunction(Symbol symbol, int offset) {
        super(symbol);
        this.offset = offset;
    }

    public static GetLocalFunction of(Symbol symbol, int offset) {
        return new GetLocalFunction(symbol, offset);
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
