package saka1029.executable;

public class GetGlobalVariable extends SymbolMacro {

    GetGlobalVariable(Symbol symbol) {
        super(symbol);
    }

    public static GetGlobalVariable of(Symbol symbol) {
        return new GetGlobalVariable(symbol);
    }

    @Override
    public void execute(Context c) {
        Executable e = c.globals.get(symbol);
        if (e == null)
            throw new RuntimeException("Symbol '%s' is not defined".formatted(this));
        c.push(e);
    }

    @Override
    public String toString() {
        return "@ " + symbol;
    }
}
