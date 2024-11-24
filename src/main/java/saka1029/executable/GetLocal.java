package saka1029.executable;

public class GetLocal extends SymbolMacro {

    final int offset;
    final boolean isFunction;

    GetLocal(Symbol symbol, int offset, boolean isFunction) {
        super(symbol);
        this.offset = offset;
        this.isFunction = isFunction;
    }

    public static GetLocal of(Symbol symbol, int offset, boolean isFunction) {
        return new GetLocal(symbol, offset, isFunction);
    }

    @Override
    public void execute(Context c) {
        if (isFunction)
            c.stack.get(c.fp + offset).execute(c); // self
        else
            c.push(c.stack.get(c.fp + offset));
    }

    @Override
    public String toString() {
        return symbol.toString();
    }
}
