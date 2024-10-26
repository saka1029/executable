package saka1029.executable;

public abstract class SymbolMacro implements Executable {
    public final Symbol symbol;

    protected SymbolMacro(Symbol symbol) {
        this.symbol = symbol;
    }
}
