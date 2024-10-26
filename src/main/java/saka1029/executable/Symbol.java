package saka1029.executable;

import java.util.HashMap;
import java.util.Map;

public class Symbol implements Executable {
    static final Map<String, Symbol> symbols = new HashMap<>();

    public final String name;

    private Symbol(String name) {
        this.name = name;
    }

    public static Symbol of(String name) {
        return symbols.computeIfAbsent(name, Symbol::new);
    }

    @Override
    public void execute(Context c) {
        Executable e = c.globals.get(this);
        if (e == null)
            throw new RuntimeException("Symbol '%s' is not defined".formatted(this));
        e.execute(c);
    }

    @Override
    public String toString() {
        return name;
    }
}
