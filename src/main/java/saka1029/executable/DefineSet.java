package saka1029.executable;

public class DefineSet extends SymbolMacro {

    DefineSet(Symbol symbol) {
        super(symbol);
    }

    public static DefineSet of(Symbol symbol) {
        return new DefineSet(symbol);
    }

    @Override
    public void execute(Context c) {
        if (!c.globals.containsKey(symbol))
            throw new RuntimeException("Symbol '%s' not defined".formatted(symbol));
        Executable body = c.pop();  // defineしたときの定義内容を保存
        c.globals.put(symbol, DefinedBody.of(body));
    }

    @Override
    public String toString() {
        return "! " + symbol;
    }
}
