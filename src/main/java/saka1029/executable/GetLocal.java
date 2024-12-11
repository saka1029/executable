package saka1029.executable;

public class GetLocal extends SymbolMacro {

    final FrameOffset position;

    GetLocal(Symbol symbol, FrameOffset offset) {
        super(symbol);
        this.position = offset;
    }

    public static GetLocal of(Symbol symbol, FrameOffset offset) {
        return new GetLocal(symbol, offset);
    }

    @Override
    public void execute(Context c) {
        int fp = c.fp(position.frame); 
        if (position.type == DefineType.FUNCTION)
            c.stack.get(fp + position.offset).execute(c);
        else
            c.push(c.stack.get(fp + position.offset));
    }

    @Override
    public String toString() {
        return "%s@%d".formatted(symbol, position);
    }
}
