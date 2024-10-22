package saka1029.executable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;

public class Context{

    java.util.List<Executable> stack = new ArrayList<>();

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
}
