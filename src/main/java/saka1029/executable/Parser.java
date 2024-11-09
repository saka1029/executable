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

        LocalContext(java.util.List<Symbol> arguments, int returnSize) {
            this.argumentSize = arguments.size();
            for (int i = this.argumentSize - 1, j = -1; i >= 0; --i, --j)
                this.locals.put(arguments.get(i), j);
            this.returnSize = returnSize;
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
        if (pc.isEmpty())  // トップレベルなら大域変数
            return DefineGlobal.of(symbol);
        LocalContext lc = pc.getLast();
        if (lc.locals.containsKey(symbol))
            throw error("Symbol '%s' is alreday defined", symbol);
        int offset = lc.localVariable(symbol);
        return DefineLocal.of(symbol, offset);
    }

    SymbolMacro set(Deque<LocalContext> pc) {
        get(); // skip '!'
        Executable e = read(pc);
        if (!(e instanceof Symbol symbol))
            throw error("Symbol expected after '!' but '%s'", e);
        if (pc.isEmpty())  // トップレベルなら大域変数
            return SetGlobal.of(symbol);
        LocalContext lc = pc.getLast();
        if (lc.locals.containsKey(symbol))  // ローカルになければ大域変数
            return SetGlobal.of(symbol);
        return SetLocal.of(symbol, lc.locals.get(symbol));
    }

    static String chString(int ch) {
        return ch == -1 ? "EOS" : "'%s'".formatted(Character.toUpperCase(ch));
    }

    static boolean isWord(int ch) {
        return switch (ch) {
            case -1, '(', ')', '[', ']' -> false;
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

    Frame frame(Deque<LocalContext> pc) {
        get(); // skip '['
        spaces();  // skip spaces after '['
        java.util.List<Symbol> arguments = new ArrayList<>();
        while (ch != -1 && ch != ']' && ch != '-' && ch != ':') {
            Executable e = read(pc);
            if (!(e instanceof Symbol s))
                throw error("Symbol expected but '%s'", e);
            arguments.add(s);
            spaces();
        }
        if (ch != '-')
            throw error("'-' expected but %s", chString(ch));
        get(); // skip '-'
        spaces(); // skip spaces after '-'
        java.util.List<Symbol> returns = new ArrayList<>();
        while (ch != -1 && ch != ']' && ch != ':') {
            Executable e = read(pc);
            if (!(e instanceof Symbol s))
                throw error("Symbol expected but '%s'", e);
            returns.add(s);
            spaces();
        }
        if (ch != ':')
            throw error("':' expected but %s", chString(ch));
        get(); // skip ':'
        spaces();
        LocalContext lc = new LocalContext(arguments, returns.size());
        pc.add(lc);
        while (ch != -1 && ch != ']') {
            lc.instructions.add(read(pc));
            spaces();
        }
        if (ch != ']')
            throw error("']' expected but %s", chString(ch));
        get(); // skip ']'
        pc.removeLast();
        return new Frame(arguments.size(), lc.localOffset, returns.size(), Cons.list(lc.instructions));
    }

    Executable read(Deque<LocalContext> pc) {
        spaces();
        return switch (ch) {
            case -1 -> throw error("Unexpected end of input");
            case '=' -> define(pc);
            case '!' -> set(pc);
            case '(' -> list(pc);
            case ')' -> throw error("Unexpected ')'");
            case '[' -> frame(pc);
            case ']' -> throw error("Unexpected ']'");
            default -> word(pc);
        };
    }
}
