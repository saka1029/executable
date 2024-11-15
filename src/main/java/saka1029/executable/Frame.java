package saka1029.executable;

import java.util.AbstractList;
import java.util.Iterator;

/**
 * Frame:
 * 引数の数をn、ローカル変数の数をm、戻り値の数をoとする。
 * <br>
 * [Frame起動直後]
 * <pre>
 * arg1     <- fp-n
 * ...
 * argn     <- fp-1
 * self     <- fp
 * local1   <- fp+1
 * ...
 * localm   <- fp+m 
 *                       <-sp
 * </pre>
 * [Frame終了直前]
 * <pre>
 * arg1     <- fp-n
 * ...
 * argn     <- fp-1
 * self     <- fp
 * local1   <- fp+1
 * ...
 * localm   <- fp+m
 * ...
 * ret1
 * ...
 * reto
 *                       <-sp
 * </pre>
 * [Frame終了時]
 * <pre>
 * ret1
 * ...
 * reto
 *                       <-sp
 * </pre>
 */
public class Frame implements Value {
    
    final int arguments, locals, returns;
    final java.util.List<Executable> body;

    Frame(int arguments, int locals, int returns, java.util.List<Executable> body) {
        this.arguments = arguments;
        this.locals = locals;
        this.returns = returns;
        this.body = body;
    }

    @Override
    public void call(Context context) {
        var save = new Object() { int oldFp, fp; };
        Executable prolog = c -> {
            // save fp
            save.oldFp = c.fp;
            save.fp = c.fp = c.stack.size();
            // push self
            c.stack.add(DefinedBody.of(this));
            // initialize locals
            for (int i = 0; i < locals; ++i)
                c.stack.add(null);
        };
        Executable epilog = c -> {
            // move return values
            int from = c.stack.size() - returns;
            int to = save.fp - arguments;
            for (int i = 0; i < returns; ++i, ++from, ++to)
                c.stack.set(to, c.stack.get(from));
            // drop stack
            while (c.stack.size() > to)
                c.stack.remove(c.stack.size() - 1);
            // restore fp
            c.fp = save.oldFp;
        };
        context.executables.addLast(new Iterator<>() {
            int index = 0, size = body.size() + 2;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public Executable next() {
                return ++index == 1 ? prolog
                    : index == size ? epilog
                    : body.get(index - 2);
            }
        });
    }

    /******************
    @Override
    public void call(Context c) {
        // save fp
        int oldFp = c.fp;
        int fp = c.fp = c.stack.size();
        // push self
        c.stack.add(DefinedBody.of(this));
        // initialize locals
        for (int i = 0; i < locals; ++i)
            c.stack.add(null);
        // execute
        c.executables.addLast(body.iterator());
        // move return values
        int from = c.stack.size() - returns;
        int to = fp - arguments;
        for (int i = 0; i < returns; ++i, ++from, ++to)
            c.stack.set(to, c.stack.get(from));
        // drop stack
        while (c.stack.size() > to)
            c.stack.remove(c.stack.size() - 1);
        // restore fp
        c.fp = oldFp;
    }
    *******************/

}
