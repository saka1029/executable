package saka1029.executable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
// import java.util.logging.Level;
import java.util.logging.Logger;

import saka1029.Common;

import static saka1029.executable.Helper.*;

public class Context{

    static final Logger logger = Common.logger(Context.class);

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

    public void dup() {
        push(stack.get(stack.size() - 1));
    }

    Deque<Iterator<Executable>> executables = new ArrayDeque<>();

    // public void call(Iterator<Executable> body) {
    //     executables.addLast(body);
    // }

    public void run(List list) {
        list.execute(this);
        pop().call(this);
        run();
    }

    public Executable eval(List list) {
        run(list);
        return pop();
    }

    public void run() {
        L: while (!executables.isEmpty()) {
            int currentSize = executables.size();
            for (Iterator<Executable> it = executables.getLast(); it.hasNext(); ) {
                Executable e = it.next();
                // Common.log(logger, Level.INFO, "run execute before: context=%s execute=%s size=%d", this, e, currentSize);
                e.execute(this);
                // Common.log(logger, Level.INFO, "run execute after : context=%s size=%d", this, executables.size());
                if (executables.size() != currentSize)
                    continue L;
            }
            executables.removeLast();
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
        add("dup", c -> c.dup());
        add("+", c -> c.push(i(i(c.pop()) + i(c.pop()))));
        add("*", c -> c.push(i(i(c.pop()) * i(c.pop()))));
        add("-", c -> { Executable r = c.pop(); c.push(i(i(c.pop()) + i(r))); });
        add("/", c -> { Executable r = c.pop(); c.push(i(i(c.pop()) / i(r))); });
        add("%", c -> { Executable r = c.pop(); c.push(i(i(c.pop()) % i(r))); });
        add("call", c -> c.pop().call(c));
        add("if", c-> {
            Executable otherwise = c.pop(), then = c.pop();
            if (b(c.pop()))
                then.call(c);
            else
                otherwise.call(c);
        });
    }
}
