package saka1029.executable;

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
    final List body;

    Frame(int arguments, int locals, int returns, List body) {
        this.arguments = arguments;
        this.locals = locals;
        this.returns = returns;
        this.body = body;
    }

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

}
