package saka1029.executable;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Parser {

    int[] input;
    int index;
    int ch;

    public List parse(String input) {
        this.input = input.codePoints().toArray();
        this.index = 0;
        get();
        java.util.List<Executable> list = new ArrayList<>();
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

    static boolean isWord(int ch) {
        return switch (ch) {
            case -1, '(', ')' -> false;
            default -> true;
        };
    }

    List list() {
        get(); // skip '('
        java.util.List<Executable> list = new ArrayList<>();
        return Cons.list(list);
    }

    static final Pattern INT_PATTERN = Pattern.compile("[+-]?\\d+");

    Executable word() {
        StringBuilder sb = new StringBuilder();
        while (isWord(ch)) {
            sb.append(ch);
            get();
        }
        String word = sb.toString();
        return INT_PATTERN.matcher(word).matches()
            ? Int.of(Integer.parseInt(word))
            : Symbol.of(word);
    }

    Executable read() {
        spaces();
        return switch (ch) {
            case -1 -> throw error("Unexpected end of input");
            case '(' -> list();
            case ')' -> throw error("Unexpected ')'");
            default -> word();
        };
    }
}
