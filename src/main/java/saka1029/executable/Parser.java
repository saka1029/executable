package saka1029.executable;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * SYNTAX
 * <pre><code>
 * program         = { element }
 * element         = int | list | symbol | define-function | define-variable | set
 * int             = [ '+' | '-' ] INT { INT }
 * list            = '(' { element} ')'
 * symbol          = SYM { SYM }
 * define-function = 'function' symbol
 * define-variable = 'variable' symbol
 * set             = 'set' symbol
 * INT             = '0' .. '9'
 * SYM             = {any charcter excludes '(' and ')'}
 * </code></pre> 
 */
public class Parser {

    static final Symbol FUNCTION = Symbol.of("function");
    static final Symbol VARIABLE = Symbol.of("variable");
    static final Symbol SET = Symbol.of("set");
    static final Symbol SELF = Symbol.of("self");

    int[] input;
    int index;
    int ch;

    public List parse(String input) {
        this.input = input.codePoints().toArray();
        this.index = 0;
        get();
        java.util.List<Executable> list = new ArrayList<>();
        while (ch != -1) {
            list.add(read(null));
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

    List list(Frame frame) {
        get(); // skip '('
        spaces();  // skip spaces after '('
        java.util.List<Executable> list = new ArrayList<>();
        while (ch != -1 && ch != ')') {
            list.add(read(frame));
            spaces();
        }
        if (ch != ')')
            throw error("Unexpected end of input");
        get(); // skip ')'
        return Cons.list(list);
    }

    Quote quote(Frame frame) {
        get(); // skip '\''
        return Quote.of(read(frame));
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

    SymbolMacro defineFunction(Frame frame) {
        Symbol symbol = symbol();
        Executable body = read(frame);
        if (frame == null)
            return DefineGlobal.of(symbol, DefineType.FUNCTION, body);
        FrameOffset position = Frame.find(frame, symbol);
        if (position != null)
            throw error("Local function '%s' is already defined", symbol);
        int offset = frame.addLocal(symbol, DefineType.FUNCTION);
        return DefineLocal.of(symbol, frame, offset, DefineType.FUNCTION, body);
    }

    SymbolMacro defineVariable(Frame frame) {
        Symbol symbol = symbol();
        Executable body = read(frame);
        if (frame == null)
            return DefineGlobal.of(symbol, DefineType.VARIABLE, body);
        FrameOffset position = Frame.find(frame, symbol);
        if (position != null)
            throw error("Local variable '%s' is already defined", symbol);
        int offset = frame.addLocal(symbol, DefineType.VARIABLE);
        return DefineLocal.of(symbol, frame, offset, DefineType.VARIABLE, body);
    }

    SymbolMacro set(Frame frame) {
        Symbol symbol = symbol();
        if (frame == null)
            return SetGlobal.of(symbol);
        FrameOffset position = Frame.find(frame, symbol);
        if (position != null)
            return SetLocal.of(symbol, position);
        return SetGlobal.of(symbol);
    }

    Executable word(Frame frame) {
        StringBuilder sb = new StringBuilder();
        while (isWord(ch)) {
            sb.appendCodePoint(ch);
            get();
        }
        String word = sb.toString();
        if (INT_PATTERN.matcher(word).matches())
            return Int.of(Integer.parseInt(word));
        Symbol symbol = Symbol.of(word);
        if (symbol.equals(SELF))
            return Self.of(frame);
        else if (symbol.equals(FUNCTION))
            return defineFunction(frame);
        else if (symbol.equals(VARIABLE))
            return defineVariable(frame);
        else if (symbol.equals(SET))
            return set(frame);
        if (frame == null)
            return symbol;
        FrameOffset position = Frame.find(frame, symbol);
        if (position != null)
            return GetLocal.of(symbol, position.frame, position.offset, false);
        return symbol;
    }

    Frame frame(Frame frame) {
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
        Frame newFrame = Frame.of(frame, arguments, returns.size(), header.toString());
        // LocalContext lc = new LocalContext(arguments, returns.size());
        while (ch != -1 && ch != ']') {
            newFrame.body.add(read(newFrame));
            spaces();
        }
        if (ch != ']')
            throw error("']' expected but %s", chString(ch));
        get(); // skip ']'
        return newFrame;
    }

    Executable read(Frame frame) {
        spaces();
        return switch (ch) {
            case -1 -> throw error("Unexpected end of input");
            case '\'' -> quote(frame);
            case '(' -> list(frame);
            case ')' -> throw error("Unexpected ')'");
            case '[' -> frame(frame);
            case ']' -> throw error("Unexpected ']'");
            default -> word(frame);
        };
    }
}
