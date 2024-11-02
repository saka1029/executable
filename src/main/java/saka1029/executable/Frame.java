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
 * old fp   <- fp
 * self     <- fp+1
 * local1   <- fp+2
 * ...
 * localm   <- fp+m+1 
 *                       <-sp
 * </pre>
 * [Frame終了直前]
 * <pre>
 * arg1     <- fp-n
 * ...
 * argn     <- fp-1
 * old fp   <- fp
 * self     <- fp+1
 * local1   <- fp+2
 * ...
 * localm   <- fp+m+1 
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
public class Frame implements Executable {
    
    final int arguments, locals, returns;
    final List executables;

    Frame(int arguments, int locals, int returns, List executables) {
        this.arguments = arguments;
        this.locals = locals;
        this.returns = returns;
        this.executables = executables;
    }

    @Override
    public void execute(Context c) {
        c.fstack.add(c.stack.size());
        executables.execute(c);
        // move return values in stack
        int sp = c.stack.size(), fp = c.fstack.get(c.fstack.size() - 1), r = fp + returns;
        for (int i = sp - returns, j = fp; i < returns; i++, j++)
            c.stack.set(j, c.stack.get(i));
        // drop stack
        while (c.stack.size() > r)
            c.pop();
        c.fstack.remove(c.fstack.size() - 1);
    }

}
