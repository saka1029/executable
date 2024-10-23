package saka1029.executable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Context{

    java.util.List<Executable> stack = new ArrayList<>();

    private Context() {
        initialize();
    }

    public static Context of() {
        return new Context();
    }

    public void push(Executable e) {
        stack.add(e);
    }

    public Executable pop() {
        return stack.remove(stack.size() - 1);
    }

    Deque<Iterator<Executable>> executables = new ArrayDeque<>();

    public void call(Iterator<Executable> body) {
        executables.addLast(body);
    }

    @Override
    public String toString() {
        return stack.toString();
    }

    final Map<Symbol, Executable> globals = new HashMap<>();

    void add(String name, Executable e) {
        globals.put(Symbol.of(name), e);
    }

    private void initialize() {
        add("+", c -> c.push(Int.of(((Int)c.pop()).value + ((Int)c.pop()).value)));
    }
}
