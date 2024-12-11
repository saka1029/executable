package saka1029.executable;

public class GetLocal extends SymbolMacro {

    final FrameOffset offset;

    GetLocal(Symbol symbol, FrameOffset offset) {
        super(symbol);
        this.offset = offset;
    }

    public static GetLocal of(Symbol symbol, FrameOffset offset) {
        return new GetLocal(symbol, offset);
    }

    @Override
    public void execute(Context c) {
        int fp = c.fp(offset.frame); 
        if (offset.type == DefineType.FUNCTION)
            c.stack.get(fp + offset.offset).execute(c);
        else
            c.push(c.stack.get(fp + offset.offset));
    }

    @Override
    public String toString() {
        return "%s@%d".formatted(symbol, offset);
    }
}
