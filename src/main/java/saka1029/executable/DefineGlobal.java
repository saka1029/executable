package saka1029.executable;

public class DefineGlobal extends SymbolMacro {

    DefineGlobal(Symbol symbol) {
        super(symbol);
    }

    public static DefineGlobal of(Symbol symbol) {
        return new DefineGlobal(symbol);
    }

    @Override
    public void execute(Context c) {
        Executable body = c.pop();  // defineしたときの定義内容を保存
        c.globals.put(symbol, DefinedBody.of(body));
    }

    @Override
    public String toString() {
        return "= " + symbol;
    }
}
