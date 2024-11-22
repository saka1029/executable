package saka1029.executable;

public class SetGlobal extends SymbolMacro {

    SetGlobal(Symbol symbol) {
        super(symbol);
    }

    public static SetGlobal of(Symbol symbol) {
        return new SetGlobal(symbol);
    }

    @Override
    public void execute(Context c) {
        if (!c.globals.containsKey(symbol))
            throw new RuntimeException("Symbol '%s' not defined".formatted(symbol));
        Executable body = c.pop();  // defineしたときの定義内容を保存
        c.globals.put(symbol, body);
    }

    @Override
    public String toString() {
        return "! " + symbol;
    }
}
