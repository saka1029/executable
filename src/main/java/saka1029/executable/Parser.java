package saka1029.executable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.Deque;
import java.util.ArrayDeque;

/**
 * SYNTAX
 * <pre><code>
 * program   = { element }
 * element   = int | list | symbol | define | set
 * int       = [ '+' | '-' ] INT { INT }
 * list      = '(' { element} ')'
 * symbol    = SYM { SYM }
 * define    = '=' symbol
 * set       = '!' symbol
 * INT       = '0' .. '9'
 * SYM       = {any charcter excludes '(' and ')'}
 * </code></pre> 
 */
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
            this.locals.put(Symbol.of("self"), 0);
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

    Symbol symbol() {
        spaces();
        StringBuilder sb = new StringBuilder();
        while (isWord(ch)) {
            sb.appendCodePoint(ch);
            get();
        }
        String word = sb.toString();
        if (word.isEmpty() || INT_PATTERN.matcher(word).matches())
            throw error("Symbol expected but '%s'", word);
        return Symbol.of(word);
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
        Symbol symbol = symbol();
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
        Symbol symbol = symbol();
        if (pc.isEmpty())  // トップレベルなら大域変数
            return SetGlobal.of(symbol);
        LocalContext lc = pc.getLast();
        if (lc.locals.containsKey(symbol))  // ローカルにあれば局所変数
            return SetLocal.of(symbol, lc.locals.get(symbol));
        return SetGlobal.of(symbol);
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
        if (CONST.containsKey(word))
            return CONST.get(word);
        if (INT_PATTERN.matcher(word).matches())
            return Int.of(Integer.parseInt(word));
        Symbol symbol = Symbol.of(word);
        if (pc.isEmpty())
            return symbol;
        LocalContext lc = pc.getLast();
        if (lc.locals.containsKey(symbol))
            return GetLocal.of(symbol, lc.locals.get(symbol));
        return symbol;
    }

    Frame frame(Deque<LocalContext> pc) {
        get(); // skip '['
        spaces();  // skip spaces after '['
        StringBuilder header = new StringBuilder();
        java.util.List<Symbol> arguments = new ArrayList<>();
        while (ch != -1 && ch != ']' && ch != '-' && ch != ':') {
            Symbol s = symbol();
            arguments.add(s);
            header.append(" ").append(s);
            spaces();
        }
        if (ch != '-')
            throw error("'-' expected but %s", chString(ch));
        get(); // skip '-'
        header.append(" -");
        spaces(); // skip spaces after '-'
        java.util.List<Symbol> returns = new ArrayList<>();
        while (ch != -1 && ch != ']' && ch != ':') {
            Symbol s = symbol();
            returns.add(s);
            header.append(" ").append(s);
            spaces();
        }
        if (ch != ':')
            throw error("':' expected but %s", chString(ch));
        get(); // skip ':'
        spaces();
        header.append(" :");
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
        return new Frame(arguments.size(), lc.localOffset, returns.size(), lc.instructions, header.substring(1));
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
