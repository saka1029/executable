package saka1029.executable;

import java.util.Collections;
import java.util.Iterator;

public interface List extends Executable, Iterable<Executable> {

    public static List NIL = new List() {
        @Override
        public Iterator<Executable> iterator() {
            return Collections.emptyIterator();
        }
    };
}
