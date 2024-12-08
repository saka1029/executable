package saka1029.executable;

public class DefineGlobal extends SymbolMacro {

    final DefineType type;

    DefineGlobal(Symbol symbol, DefineType type) {
        super(symbol);
        this.type = type;
    }

    public static DefineGlobal of(Symbol symbol, DefineType type) {
        return new DefineGlobal(symbol, type);
    }

    @Override
    public void execute(Context c) {
        Executable body = c.pop();  // defineしたときの定義内容を保存
        c.globals.put(symbol, GlobalValue.of(body, type));
    }

    @Override
    public String toString() {
        return (type == DefineType.FUNCTION ? "function " : "variale ") + symbol;
    }
}
