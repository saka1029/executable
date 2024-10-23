package saka1029.executable;

import java.util.Collections;
import java.util.Iterator;

public interface List extends Value, Iterable<Executable> {

    public static List NIL = new List() {
        @Override
        public Iterator<Executable> iterator() {
            return Collections.emptyIterator();
        }

        @Override
        public String toString() {
            return "()";
        }
    };

    default void call(Context c) {
        c.call(iterator());
    }

}
