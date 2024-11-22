package saka1029.executable;

import java.util.Collections;
import java.util.Iterator;
// import java.util.logging.Level;
import java.util.logging.Logger;

import saka1029.Common;

public interface List extends Executable, Iterable<Executable> {

    static final Logger logger = Common.logger(List.class);

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

    @Override
    default void execute(Context c) {
        c.executables.addLast(iterator());
    }
}
