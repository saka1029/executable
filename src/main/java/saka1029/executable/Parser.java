package saka1029.executable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.Deque;
import java.util.ArrayDeque;

public class Parser {

    int[] input;
    int index;
    int ch;

    static class LocalContext {

        final Map<Symbol, Integer> locals = new HashMap<>();
        int argumentSize, returnSize;
        final java.util.List<Executable> instructions = new ArrayList<>();
        int localOffset = 0;

        void begin(java.util.List<Symbol> arguments, int returnSize) {
            this.argumentSize = arguments.size();
            for (int i = this.argumentSize - 1, j = -1; i >= 0; --i, --j)
                this.locals.put(arguments.get(i), j);
            this.returnSize = returnSize;
        }

        Frame end() {
            Frame f = new Frame(argumentSize, localOffset, returnSize, Cons.list(instructions));
            return f;
        }

        int localVariable(Symbol variable) {
            int offset = ++localOffset;
            locals.put(variable, offset);
            return offset;
        }
    }

    public List parse(String input) {
        this.input = input.codePoints().toArray();
        this.index = 0;
        get();
        Deque<LocalContext> pc = new ArrayDeque<>();
        java.util.List<Executable> list = new ArrayList<>();
        while (ch != -1) {
            list.add(read(pc));
            spaces();
        }
        return Cons.list(list);
    }

    int get() {
        return ch = index < input.length ? input[index++] : -1;
    }

    RuntimeException error(String format, Object... args) {
        return new RuntimeException(format.formatted(args));
    }

    void spaces() {
        while (Character.isWhitespace(ch))
            get();
    }

    List list(Deque<LocalContext> pc) {
        get(); // skip '('
        spaces();  // skip spaces after '('
        java.util.List<Executable> list = new ArrayList<>();
        while (ch != -1 && ch != ')') {
            list.add(read(pc));
            spaces();
        }
        if (ch != ')')
            throw error("Unexpected end of input");
        get(); // skip ')'
        return Cons.list(list);
    }

    SymbolMacro define(Deque<LocalContext> pc) {
        get(); // skip '='
        Executable e = read(pc);
        if (!(e instanceof Symbol symbol))
            throw error("Symbol expected after '=' but '%s'", e);
        if (pc.isEmpty())  // トップレベルなら大域定義
            return Define.of(symbol);
        LocalContext lc = pc.getLast();
        if (lc.locals.containsKey(symbol))
            throw error("Symbol '%s' is alreday defined", symbol);
        int offset = lc.localVariable(symbol);
        return DefineLocal.of(symbol, offset);
    }

    DefineSet set(Deque<LocalContext> pc) {
        get(); // skip '!'
        Executable e = read(pc);
        if (!(e instanceof Symbol symbol))
            throw error("Symbol expected after '!' but '%s'", e);
        return DefineSet.of(symbol);
    }

    static boolean isWord(int ch) {
        return switch (ch) {
            case -1, '(', ')' -> false;
            default -> !Character.isWhitespace(ch);
        };
    }

    static final Pattern INT_PATTERN = Pattern.compile("[+-]?\\d+");
    static final Map<String, Executable> CONST = Map.of(
        "true", Bool.TRUE,
        "false", Bool.FALSE
    );

    Executable word(Deque<LocalContext> pc) {
        StringBuilder sb = new StringBuilder();
        while (isWord(ch)) {
            sb.appendCodePoint(ch);
            get();
        }
        String word = sb.toString();
        return CONST.containsKey(word) ? CONST.get(word)
            : INT_PATTERN.matcher(word).matches() ? Int.of(Integer.parseInt(word))
            : Symbol.of(word);
    }

    Executable read(Deque<LocalContext> pc) {
        spaces();
        return switch (ch) {
            case -1 -> throw error("Unexpected end of input");
            case '(' -> list(pc);
            case '=' -> define(pc);
            case '!' -> set(pc);
            case ')' -> throw error("Unexpected ')'");
            default -> word(pc);
        };
    }
}
