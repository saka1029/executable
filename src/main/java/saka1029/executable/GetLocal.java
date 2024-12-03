package saka1029.executable;

public class GetLocal extends SymbolMacro {

    final Frame frame;
    final int offset;
    final boolean isFunction;

    GetLocal(Symbol symbol, Frame frame, int offset, boolean isFunction) {
        super(symbol);
        this.frame = frame;
        this.offset = offset;
        this.isFunction = isFunction;
    }

    public static GetLocal of(Symbol symbol, Frame frame, int offset, boolean isFunction) {
        return new GetLocal(symbol, frame, offset, isFunction);
    }

    @Override
    public void execute(Context c) {
        int fp = c.fp(frame); 
        if (isFunction)
            c.stack.get(fp + offset).execute(c); // self
        else
            c.push(c.stack.get(fp + offset));
    }

    @Override
    public String toString() {
        return "%s@%d".formatted(symbol, offset);
    }
}
