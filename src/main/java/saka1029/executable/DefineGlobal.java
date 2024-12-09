package saka1029.executable;

public class DefineGlobal extends SymbolMacro {

    final DefineType type;
    final Executable value;

    DefineGlobal(Symbol symbol, DefineType type, Executable value) {
        super(symbol);
        this.type = type;
        this.value = value;
    }

    public static DefineGlobal of(Symbol symbol, DefineType type, Executable value) {
        return new DefineGlobal(symbol, type, value);
    }

    @Override
    public void execute(Context context) {
        Executable epilog = c -> {
            Executable body = c.pop();  // defineしたときの定義内容を保存
            c.globals.put(symbol, GlobalValue.of(body, type));
        };
        context.executables.addLast(java.util.List.of(value, epilog).iterator());
    }

    @Override
    public String toString() {
        return "%s %s %s".formatted(
            type == DefineType.FUNCTION ? "function " : "variale ", symbol, value);
    }
}
