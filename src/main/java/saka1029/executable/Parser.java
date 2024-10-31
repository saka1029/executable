package saka1029.executable;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.Map;

public class Parser {

    int[] input;
    int index;
    int ch;

    public List parse(String input) {
        this.input = input.codePoints().toArray();
        this.index = 0;
        get();
        ParseContext pc = new ParseContext();
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

    List list(ParseContext pc) {
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

    Define define(ParseContext pc) {
        get(); // skip '='
        Executable e = read(pc);
        if (!(e instanceof Symbol symbol))
            throw error("Symbol expected after '=' but '%s'", e);
        return Define.of(symbol);
    }

    DefineSet set(ParseContext pc) {
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

    Executable word(ParseContext pc) {
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

    Executable read(ParseContext pc) {
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
