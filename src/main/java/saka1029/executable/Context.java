package saka1029.executable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import static saka1029.executable.Helper.*;

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

    public void run(List list) {
        list.execute(this);
        pop().call(this);
        run();
    }

    public void run() {
        L: for ( ; !executables.isEmpty(); executables.removeLast()) {
            int currentSize = executables.size();
            for (Iterator<Executable> it = executables.getLast(); it.hasNext(); ) {
                it.next().execute(this);
                if (executables.size() != currentSize)
                    continue L;
            }
        }
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
        add("+", c -> c.push(i(i(c.pop()) + i(c.pop()))));
        add("call", c -> c.pop().call(c));
    }
}
