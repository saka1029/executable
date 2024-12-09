package saka1029.executable;

public class GetLocal extends SymbolMacro {

    final DefineType type;
    final Frame frame;
    final int offset;

    GetLocal(Symbol symbol, DefineType type, Frame frame, int offset) {
        super(symbol);
        this.frame = frame;
        this.offset = offset;
        this.type = type;
    }

    public static GetLocal of(Symbol symbol, DefineType type, Frame frame, int offset) {
        return new GetLocal(symbol, type, frame, offset);
    }

    @Override
    public void execute(Context c) {
        int fp = c.fp(frame); 
        if (type == DefineType.FUNCTION)
            c.stack.get(fp + offset).execute(c);
        else
            c.push(c.stack.get(fp + offset));
    }

    @Override
    public String toString() {
        return "%s@%d".formatted(symbol, offset);
    }
}
