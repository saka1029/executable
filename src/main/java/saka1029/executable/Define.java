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
        Executable body = c.pop();  // defineしたときの定義内容を保存
        c.globals.put(symbol, DefinedBody.of(body));
    }

    @Override
    public String toString() {
        return "= " + symbol;
    }
}