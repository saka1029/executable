package saka1029.executable;

import java.util.ArrayList;

public class Parser {

    String input;
    int index;
    StringBuilder text = new StringBuilder();

    public List parse(String input) {
        this.input = input;
        this.index = 0;
        this.text.setLength(0);
        java.util.List<Executable> list = new ArrayList<>();
        return Cons.list(list);
    }

}

