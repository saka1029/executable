package saka1029.executable;

import java.util.Collections;
import java.util.Iterator;
// import java.util.logging.Level;
import java.util.logging.Logger;

import saka1029.Common;

public interface List extends Value, Iterable<Executable> {

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

    default void call(Context c) {
        // Common.log(logger, Level.INFO, "call: context=%s list=%s", c, this);
        c.call(iterator());
    }

    public static List of(Executable... es) {
        return Cons.of(es);
    }
}
