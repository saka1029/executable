package saka1029.executable;

public class DefineGlobal extends SymbolMacro {

    final boolean isFunction;

    DefineGlobal(Symbol symbol, boolean isFunction) {
        super(symbol);
        this.isFunction = isFunction;
    }

    public static DefineGlobal of(Symbol symbol, boolean isFunction) {
        return new DefineGlobal(symbol, isFunction);
    }

    @Override
    public void execute(Context c) {
        Executable body = c.pop();  // defineしたときの定義内容を保存
        c.globals.put(symbol, Context.FuncVar.of(body, isFunction));
    }

    @Override
    public String toString() {
        return "= " + symbol;
    }
}
