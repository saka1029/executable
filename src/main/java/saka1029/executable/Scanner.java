package saka1029.executable;

import java.util.Map;

public class Scanner {

    final int[] input;
    int index = 0;
    int ch;
    final StringBuilder sb = new StringBuilder();

    final static Map<Integer, TokenType> ONE_CHAR_TOKENS = Map.of(
        (int)'(', TokenType.LP, (int)')', TokenType.RP,
        (int)'\'', TokenType.QUOTE, (int)'`', TokenType.BACK_QUOTE, (int)'$', TokenType.DOLLAR,
        (int)'.', TokenType.DOT, (int)':', TokenType.COLON
    );

    Scanner(String string) {
        this.input = string.codePoints().toArray();
        ch();
    }

    public static Scanner of(String string) {
        return new Scanner(string);
    }

    public Symbol symbol() {
        return Symbol.of(sb.toString());
    }

    public int number() {
        return Integer.parseInt(sb.toString());
    }

    int ch() {
        return ch = index < input.length ? input[index++] : -1;
    }

    void appendGet(int ch) {
        sb.appendCodePoint(ch);
        get();
    }

    static boolean isWord(int ch) {
        return switch (ch) {
            case -1, '(', ')', '\'', '`', '$', '.', ':' -> false;
            default -> !Character.isWhitespace(ch);
        };
    }

    public TokenType get() {
        while (Character.isWhitespace(ch))
            get();
        sb.setLength(0);
        switch (ch) {
            case -1: return TokenType.END;
            case '(': appendGet(ch); return TokenType.LP;
            case ')': appendGet(ch); return TokenType.RP;
            case '\'': appendGet(ch); return TokenType.QUOTE;
            case '`': appendGet(ch); return TokenType.BACK_QUOTE;
            case '$': appendGet(ch); return TokenType.DOLLAR;
            case '.': appendGet(ch); return TokenType.DOT;
            case ':': appendGet(ch); return TokenType.COLON;
            case '-': appendGet(ch); /* thru */
            default:
                if (Character.isDigit(ch)) {
                    while (Character.isDigit(ch))
                        appendGet(ch);
                    return TokenType.NUMBER;
                } else {
                    while (isWord(ch))
                        appendGet(ch);
                    return TokenType.SYMBOL;
                }
        }
    }
}
