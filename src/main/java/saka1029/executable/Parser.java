package saka1029.executable;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * SYNTAX
 * <pre><code>
 * program          = { element }
 * element          = int | list | symbol | quote | list-constructor
 *                  | define-function | define-variable | set | frame
 * int              = [ "+" | "-" ] INT { INT }
 * list             = "(" { element} ")"
 * symbol           = SYM { SYM }
 * quote            = "'" element
 * list-constructor = "`" element
 * define-function  = "function" symbol element
 * define-variable  = "variable" symbol element
 * set              = "set" symbol
 * frame            = "[" { symbol } "-" { symbol } ":" { element } "]"
 * INT              = "0" .. "9"
 * SYM              = {any charcter excludes white spaces, "(", ")", "[", "]", "'", "`", ".", ":"}
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

    ListConstructor listConstructor(Frame frame) {
        get(); // skip '`'
        return ListConstructor.of(read(frame));
    }

    static String chString(int ch) {
        return ch == -1 ? "EOS" : "'%s'".formatted(Character.toString(ch));
    }

    static boolean isWord(int ch) {
        return switch (ch) {
            case -1, '(', ')', '[', ']', '\'', '`' -> false;
            default -> !Character.isWhitespace(ch);
        };
    }

    static final Pattern INT_PATTERN = Pattern.compile("[+-]?\\d+");

    Self self(Frame frame) {
        if (frame == null)
            throw error("'self' not in frame");
        return Self.of(frame);
    }

    SymbolMacro defineFunction(Frame frame) {
        Symbol symbol = symbol();
        if (frame == null) // Frame外なら大域関数定義
            return DefineGlobal.of(symbol, DefineType.FUNCTION, read(frame));
        FrameOffset position = Frame.find(frame, symbol);
        if (position != null)
            throw error("Local function '%s' is already defined", symbol);
        int offset = frame.addLocal(symbol, DefineType.FUNCTION);
        // freamに定義を追加したあと関数定義本体を読み込む。(再帰呼出しがありうるため)
        Executable body = read(frame);
        return DefineLocal.of(symbol, FrameOffset.of(DefineType.FUNCTION, frame, offset), body);
    }

    SymbolMacro defineVariable(Frame frame) {
        Symbol symbol = symbol();
        if (frame == null) // Frame外なら大域変数定義
            return DefineGlobal.of(symbol, DefineType.VARIABLE, read(frame));
        FrameOffset position = Frame.find(frame, symbol);
        if (position != null)
            throw error("Local variable '%s' is already defined", symbol);
        // freamに定義を追加する前に変数値を読み込む。(再帰的定義はエラー)
        Executable body = read(frame);
        int offset = frame.addLocal(symbol, DefineType.VARIABLE);
        return DefineLocal.of(symbol, FrameOffset.of(DefineType.VARIABLE, frame, offset), body);
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
            return self(frame);
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
            return GetLocal.of(symbol, position);
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
            case '`' -> listConstructor(frame);
            case '(' -> list(frame);
            case ')' -> throw error("Unexpected ')'");
            case '[' -> frame(frame);
            case ']' -> throw error("Unexpected ']'");
            default -> word(frame);
        };
    }
}
