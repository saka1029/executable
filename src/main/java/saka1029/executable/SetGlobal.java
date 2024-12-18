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
        GlobalValue f = c.globals.get(symbol);
        if (f == null)
            throw new RuntimeException("Symbol '%s' not defined".formatted(symbol));
        Executable body = c.pop();  // defineしたときの定義内容を保存
        c.globals.put(symbol, GlobalValue.of(body, f.type));
    }

    @Override
    public String toString() {
        return "set " + symbol;
    }
}
