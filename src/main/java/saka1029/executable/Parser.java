package saka1029.executable;

import java.util.ArrayList;

/**
 * SYNTAX
 * <pre><code>
 * program          = { element }
 * element          = int | list | symbol | quote | list-constructor
 *                  | function | variable | set | frame
 * int              = [ "+" | "-" ] INT { INT }
 * list             = "(" { element} ")"
 * symbol           = SYM { SYM }
 * quote            = "'" element
 * list-constructor = "`" element
 * function         = "function" symbol element
 * variable         = "variable" symbol element
 * set              = "set" symbol
 * frame            = "[" { symbol } "." { symbol } ":" { element } "]"
 * INT              = "0" .. "9"
 * SYM              = {any charcter excludes white spaces, "(", ")", "[", "]", "'", "`", ".", ":"}
 * </code></pre> 
 */
public class Parser {

    static final Symbol FUNCTION = Symbol.of("function");
    static final Symbol VARIABLE = Symbol.of("variable");
    static final Symbol SET = Symbol.of("set");
    static final Symbol SELF = Symbol.of("self");

    Scanner scanner;
    TokenType type;

    public List parse(String input) {
        this.scanner = Scanner.of(input);
        get();
        java.util.List<Executable> list = new ArrayList<>();
        while (type != TokenType.END)
            list.add(read(null));
        return Cons.list(list);
    }

    TokenType get() {
        return type = scanner.get();
    }

    RuntimeException error(String format, Object... args) {
        return new RuntimeException(format.formatted(args));
    }

    Symbol symbol() {
        if (type != TokenType.SYMBOL)
            throw error("Symbol expected but '%s'", scanner.string());
        Symbol symbol = scanner.symbol();
        get(); // skip symbol
        return symbol;
    }

    List list(Frame frame) {
        get(); // skip '('
        java.util.List<Executable> list = new ArrayList<>();
        while (type != TokenType.END && type != TokenType.RP) {
            list.add(read(frame));
        }
        if (type != TokenType.RP)
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

    Self self(Frame frame) {
        if (frame == null)
            throw error("'self' not in frame");
        return Self.of(frame);
    }

    SymbolMacro function(Frame frame) {
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

    SymbolMacro variable(Frame frame) {
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

    Int number(Frame frame) {
        Int number = scanner.number();
        get(); // skip number
        return number;
    }

    Executable symbol(Frame frame) {
        Symbol symbol = scanner.symbol();
        get();
        if (symbol.equals(SELF))
            return self(frame);
        else if (symbol.equals(FUNCTION))
            return function(frame);
        else if (symbol.equals(VARIABLE))
            return variable(frame);
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
        StringBuilder header = new StringBuilder();
        java.util.List<Symbol> arguments = new ArrayList<>();
        while (type != TokenType.END && type != TokenType.RB
            && type != TokenType.DOT && type != TokenType.COLON) {
            Symbol s = symbol();
            arguments.add(s);
            header.append(" ").append(s);
        }
        if (type != TokenType.DOT)
            throw error("'.' expected but '%s'", scanner.string());
        get(); // skip '.'
        header.append(" .");
        java.util.List<Symbol> returns = new ArrayList<>();
        while (type != TokenType.END && type != TokenType.RB
            && type != TokenType.COLON) {
            Symbol s = symbol();
            returns.add(s);
            header.append(" ").append(s);
        }
        if (type != TokenType.COLON)
            throw error("':' expected but '%s'", scanner.string());
        get(); // skip ':'
        header.append(" :");
        Frame newFrame = Frame.of(frame, arguments, returns.size(), header.toString());
        // LocalContext lc = new LocalContext(arguments, returns.size());
        while (type != TokenType.END && type != TokenType.RB)
            newFrame.body.add(read(newFrame));
        if (type != TokenType.RB)
            throw error("']' expected but '%s'", scanner.string());
        get(); // skip ']'
        return newFrame;
    }

    Executable read(Frame frame) {
        return switch (type) {
            case END -> throw error("Unexpected end of input");
            case QUOTE -> quote(frame);
            case BACK_QUOTE -> listConstructor(frame);
            case LP -> list(frame);
            case LB -> frame(frame);
            case NUMBER -> number(frame);
            case SYMBOL -> symbol(frame);
            case RP, RB, DOT, COLON -> throw error("Unexpected '%s'", scanner.string());
        };
    }
}
