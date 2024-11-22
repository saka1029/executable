package saka1029.executable;

public class GetLocalVariable extends SymbolMacro {

    final int offset;

    GetLocalVariable(Symbol symbol, int offset) {
        super(symbol);
        this.offset = offset;
    }

    public static GetLocalVariable of(Symbol symbol, int offset) {
        return new GetLocalVariable(symbol, offset);
    }

    @Override
    public void execute(Context c) {
        c.push(c.stack.get(c.fp + offset));
    }

    @Override
    public String toString() {
        return "@ " + symbol;
    }
}
