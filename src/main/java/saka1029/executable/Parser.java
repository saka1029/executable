package saka1029.executable;

import java.util.ArrayList;

public class Parser {

    int[] input;
    int index;
    StringBuilder text = new StringBuilder();
    int ch;

    public List parse(String input) {
        this.input = input.codePoints().toArray();
        this.index = 0;
        this.text.setLength(0);
        get();
        java.util.List<Executable> list = new ArrayList<>();
        return Cons.list(list);
    }

    int get() {
        return ch = index < input.length ? input[index++] : -1;
    }

    Executable read() {
        return switch (ch) {
            case -1 -> null;
            default -> throw new RuntimeException();
        };
    }

}
