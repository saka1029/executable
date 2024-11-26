package saka1029.executable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
// import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

    public void dup(int index) {
        push(stack.get(stack.size() - 1 - index));
    }

    public void swap() {
        int top = stack.size() - 1, second = top - 1;
        Collections.swap(stack, top, second);
    }

    public void drop() {
        stack.remove(stack.size() - 1);
    }

    public void rot() {
        int i0 = stack.size() - 1, i1 = i0 - 1, i2 = i0 - 2;
        Executable top = stack.get(i0);
        stack.set(i0, stack.get(i2));
        stack.set(i2, stack.get(i1));
        stack.set(i1, top);
    }

    int fp = 0;

    Deque<Iterator<Executable>> executables = new ArrayDeque<>();

    public void run(List list) {
        // Common.log(logger, Level.INFO, "run: %s", list);
        list.execute(this);
        run();
    }

    public Executable eval(List list) {
        run(list);
        Executable result = pop();
        // Common.log(logger, Level.INFO, "eval: returns %s", result);
        return result;
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
        return stack.stream()
            .map(e -> e.toString())
            .collect(Collectors.joining(", ", "{", "}"));
    }

    final Map<Symbol, FunctionVariable> globals = new HashMap<>();

    void addVariable(String name, Executable e) {
        globals.put(Symbol.of(name), FunctionVariable.of(e, false));
    }

    void add(String name, Executable e) {
        globals.put(Symbol.of(name), FunctionVariable.of(e, true));
    }

    private void initialize() {
        addVariable("true", TRUE);
        addVariable("false", FALSE);
        addVariable("nil", NIL);
        add("dup", c -> c.dup(0));
        add("dup1", c -> c.dup(1));
        add("dup2", c -> c.dup(2));
        add("swap", c -> c.swap());
        add("drop", c -> c.drop());
        add("rot", c -> c.rot());
        add("+", c -> c.push(i(asI(c.pop(), "+(right)") + asI(c.pop(), "+(left)"))));
        add("*", c -> c.push(i(asI(c.pop(), "*(right)") * asI(c.pop(), "*(left)"))));
        add("-", c -> c.push(i(-asI(c.pop(), "-(right)") + asI(c.pop(), "-(left)"))));
        add("/", c -> { Executable r = c.pop(); c.push(i(asI(c.pop(), "/(left)") / asI(r, "/(right)"))); });
        add("%", c -> { Executable r = c.pop(); c.push(i(asI(c.pop(), "%(left)") % asI(r, "%(right)"))); });
        add("==", c -> c.push(b(c.pop().equals(c.pop()))));
        add("!=", c -> c.push(b(!c.pop().equals(c.pop()))));
        add("<", c -> c.push(b(asComp(c.pop(), "<(right)").compareTo(asComp(c.pop(), "<(left)")) > 0)));
        add("<=", c -> c.push(b(asComp(c.pop(), "<=(right)").compareTo(asComp(c.pop(), "<=(left)")) >= 0)));
        add(">", c -> c.push(b(asComp(c.pop(), ">(right)").compareTo(asComp(c.pop(), ">(left)")) < 0)));
        add(">=", c -> c.push(b(asComp(c.pop(), ">=(right)").compareTo(asComp(c.pop(), ">=(left)")) <= 0)));
        add("and", c -> c.push(b(asBool(c.pop(), "and(right)") & asBool(c.pop(), "and(left)"))));
        add("or", c -> c.push(b(asBool(c.pop(), "or(right)") | asBool(c.pop(), "or(left)"))));
        add("not", c-> c.push(b(!asBool(c.pop(), "not"))));
        add("print", c -> System.out.println(c.pop()));
        add("stack", c -> System.out.println(c));
        add("call", c -> {
            Executable top = c.pop();
            top.execute(c);
        });
        add("if", c-> {
            Executable otherwise = c.pop(), then = c.pop();
            if (asBool(c.pop(), "if"))
                then.execute(c);
            else
                otherwise.execute(c);
        });
        add("for", c -> {
            // LIST BLOCK each
            Executable block = c.pop();
            Iterator<Executable> iterator = asList(c.pop(), "for").iterator();
            executables.addLast(new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }
                @Override
                public Executable next() {
                    return c -> {
                        c.push(iterator.next());
                        block.execute(c);
                    };
                }
            });
        });
        add("while", c -> {
            Executable body = c.pop(), cond = c.pop();
            executables.addLast(new Iterator<>() {
                boolean cont = true;
                int step = 0;
                @Override
                public boolean hasNext() {
                    return cont;
                }
                @Override
                public Executable next() {
                    if (step++ % 2 == 0)
                        return cond;
                    if (cont = asBool(c.pop(), "while"))
                        return body;
                    cont = false;
                    return NIL;  // do nothing
                }
            });
        });
        add("filter", c -> {
            Executable pred = c.pop(), list = asList(c.pop(), "filter");
            c.instructions.addLast(new Iterator<>() {
                Iterator<Executable> it = list.iterator();
                boolean cont = true;
                int step = 0;
                Executable e = null;
                @Override
                public boolean hasNext() {
                }
                @Override
                public Executable next() {
                }
            });
        });
        add("car", c -> c.push(asCons(c.pop(), "car").car));
        add("cdr", c -> c.push(asCons(c.pop(), "cdr").cdr));
        add("cons", c -> {
            List cdr = asList(c.pop(), "cons");
            Executable car = c.pop();
            c.push(Cons.of(car, cdr));
        });
        add("rcons", c -> {
            Executable car = c.pop();
            List cdr = asList(c.pop(), "rcons");
            c.push(Cons.of(car, cdr));
        });
        add("null", c -> c.push(b(c.pop() == NIL)));
        // add("nil", c -> c.push(list(NIL)));
        add("uncons", c -> {
            Cons cons = asCons(c.pop(), "uncons");
            c.push(cons.car);
            c.push(cons.cdr);
        });
        add("reverse", c -> {
            List list = asList(c.pop(), "reverse"), result = NIL;
            for (Executable e : list)
                result = Cons.of(e, result);
            c.push(result);
        });
        add("range", c -> {
            int step = asI(c.pop(), "range(step)"), end = asI(c.pop(), "range(end)"), start = asI(c.pop(), "range(start)");
            c.push(Range.of(start, end, step));
        });
    }
}
