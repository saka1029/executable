package saka1029.executable;

public class Define extends SymbolMacro {

    Define(Symbol symbol) {
        super(symbol);
    }

    public static Define of(Symbol symbol) {
        return new Define(symbol);
    }

    @Override
    public void execute(Context c) {
        Executable body = c.pop();
        c.globals.put(symbol, x -> body.call(x));
    }

    @Override
    public String toString() {
        return "define " + symbol;
    }
}
