package saka1029.executable;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Cons implements List {

    public final Executable car;
    public final List cdr;

    Cons(Executable car, List cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    public static Cons of(Executable car, List cdr) {
        return new Cons(car, cdr);
    }

    public static List of(Executable... executables) {
        List list = List.NIL;
        for (int i = executables.length - 1; i >= 0; --i)
            list = Cons.of(executables[i], list);
        return list;
    }

    public static List of(java.util.List<Executable> executables) {
        List list = List.NIL;
        for (int i = executables.size() - 1; i >= 0; --i)
            list = Cons.of(executables.get(i), list);
        return list;
    }

    @Override
    public Iterator<Executable> iterator() {
        return new Iterator<>() {
            List list = Cons.this;

            @Override
            public boolean hasNext() {
                return list.equals(List.NIL);
            }

            @Override
            public Executable next() {
                if (!(list instanceof Cons cons))
                    throw new NoSuchElementException();
                Executable e = cons.car;
                list = cons.cdr;
                return e;
            }

        };
    }
}