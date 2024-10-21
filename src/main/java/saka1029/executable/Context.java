package saka1029.executable;

import java.util.ArrayList;

public class Context{

    java.util.List<Executable> stack = new ArrayList<>();

    public void push(Executable e) {
        stack.add(e);
    }
}
