package saka1029.executable;

import java.util.AbstractList;

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

    static class Ctx {
        int oldFp, fp;
    }

    Executable prolog(Ctx ctx) {
        return c -> {
            // save fp
            ctx.oldFp = c.fp;
            ctx.fp = c.fp = c.stack.size();
            // push self
            c.stack.add(DefinedBody.of(this));
            // initialize locals
            for (int i = 0; i < locals; ++i)
                c.stack.add(null);
        };
    }

    Executable epilog(Ctx ctx) {
        return c -> {
            // move return values
            int from = c.stack.size() - returns;
            int to = ctx.fp - arguments;
            for (int i = 0; i < returns; ++i, ++from, ++to)
                c.stack.set(to, c.stack.get(from));
            // drop stack
            while (c.stack.size() > to)
                c.stack.remove(c.stack.size() - 1);
            // restore fp
            c.fp = ctx.oldFp;
        };
    }

    class FrameBody extends AbstractList<Executable> {

        final Executable prolog, epilog;
        final java.util.List<Executable> body;

        FrameBody(Executable prolog, java.util.List<Executable> body, Executable epilog) {
            this.prolog = prolog;
            this.body = body;
            this.epilog = epilog;
        }

        @Override
        public int size() {
            return body.size() + 2;
        }

        @Override
        public Executable get(int index) {
            return index == 0 ? prolog
                : index == body.size() + 1 ? epilog
                : body.get(index - 1);
        }
    }

    @Override
    public void call(Context c) {
        Ctx ctx = new Ctx();
        c.executables.addLast(new FrameBody(prolog(ctx), body, epilog(ctx)).iterator());
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
