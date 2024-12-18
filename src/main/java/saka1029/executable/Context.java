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

    public static List append(List left, List right) {
        if (left == NIL)
            return right;
        Cons cons = asCons(left, "append");
        return Cons.of(cons.car, append(cons.cdr, right));
    }

    // int fp = 0;

    Map<Frame, Deque<Integer>> fpStack = new HashMap<>();

    void pushFp(Frame f) {
        fpStack.computeIfAbsent(f, k -> new ArrayDeque<>()).addLast(stack.size());
    }

    int popFp(Frame f) {
        return fpStack.get(f).removeLast();
    }

    int fp(Frame f) {
        return fpStack.get(f).getLast();
    }

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

    public void execute(Executable... exs) {
        executables.addLast(new Iterator<>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < exs.length;
            }

            @Override
            public Executable next() {
                return exs[index++];
            }
        });
    }

    @Override
    public String toString() {
        return stack.stream()
            .map(e -> e.toString())
            .collect(Collectors.joining(", ", "{", "}"));
    }

    final Map<Symbol, GlobalValue> globals = new HashMap<>();

    void addVariable(String name, Executable e) {
        globals.put(Symbol.of(name), GlobalValue.of(e, DefineType.VARIABLE));
    }

    void add(String name, Executable e) {
        globals.put(Symbol.of(name), GlobalValue.of(e, DefineType.FUNCTION));
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
        add("when", c-> {
            Executable then = c.pop();
            if (asBool(c.pop(), "when"))
                then.execute(c);
        });
        add("unless", c-> {
            Executable then = c.pop();
            if (!asBool(c.pop(), "unless"))
                then.execute(c);
        });
        add("for", c -> {
            // LIST BLOCK each
            Executable block = c.pop();
            Iterator<Executable> iterator = asIterable(c.pop(), "for").iterator();
            c.executables.addLast(new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }
                @Override
                public Executable next() {
                    c.push(iterator.next());
                    return block;
                }
            });
        });
        add("while", c -> {
            Executable body = c.pop(), cond = c.pop();
            c.executables.addLast(new Iterator<>() {
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
            Executable pred = c.pop();
            Iterable<Executable> list = asIterable(c.pop(), "filter");
            c.executables.addLast(new Iterator<>() {
                Iterator<Executable> it = list.iterator();
                java.util.List<Executable> result = new ArrayList<>();
                boolean cont = true;
                int step = 0;
                Executable e;
                @Override
                public boolean hasNext() {
                    return cont;
                }
                @Override
                public Executable next() {
                    if (step++ % 2 == 0) {
                        if (it.hasNext()) {
                            e = it.next();
                            c.push(e);
                            return pred;
                        } else {
                            c.push(Cons.list(result));
                            cont = false;
                            return NIL;
                        }
                    } else {
                        if (asBool(c.pop(), "filter"))
                            result.add(e);
                        return NIL;
                    }
                }
            });
        });
        add("map", c-> {
            Executable mapper = c.pop();
            Iterable<Executable> list = asIterable(c.pop(), "map");
            c.executables.addLast(new Iterator<Executable>() {
                Iterator<Executable> it = list.iterator();
                java.util.List<Executable> result = new ArrayList<>();
                boolean cont = true;
                int step = 0;
                @Override
                public boolean hasNext() {
                    return cont;
                }
                @Override
                public Executable next() {
                    if (step++ % 2 == 0) {
                        if (it.hasNext()) {
                            c.push(it.next());
                            return mapper;
                        } else {
                            c.push(Cons.list(result));
                            cont = false;
                            return NIL;
                        }
                    } else {
                        result.add(c.pop());
                        return NIL;
                    }
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
        add("append", c -> {
            List right = asList(c.pop(), "append(right)"), left = asList(c.pop(), "append(left)");
            c.push(append(left, right));
        });
        add("reverse", c -> {
            Iterable<Executable> list = asIterable(c.pop(), "reverse");
            List result = NIL;
            for (Executable e : list)
                result = Cons.of(e, result);
            c.push(result);
        });
        add("range", c -> {
            int step = asI(c.pop(), "range(step)"), end = asI(c.pop(), "range(end)"), start = asI(c.pop(), "range(start)");
            c.push(Range.of(start, end, step));
        });
        add("array", c -> {
            int size = asI(c.pop(), "array");
            Executable fill = c.pop();
            c.push(Array.of(size, fill));
        });
        add("size", c -> {
            Array array = asArray(c.pop(), "size");
            c.push(i(array.size()));
        });
        add("get", c -> {
            Array array = asArray(c.pop(), "get(array)");
            int index = asI(c.pop(), "get(index)");
            c.push(array.get(index));
        });
        add("put", c -> {
            Array array = asArray(c.pop(), "put(array)");
            int index = asI(c.pop(), "put(index)");
            Executable value = c.pop();
            array.put(index, value);
        });
    }
}
