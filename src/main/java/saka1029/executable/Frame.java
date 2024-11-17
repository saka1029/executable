package saka1029.executable;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Frame:
 * 引数の数をn、ローカル変数の数をm、戻り値の数をoとする。
 * <br>
 * [Frame起動直後]
 * <pre>
 * arg1     ← fp-n
 * ...
 * argn     ← fp-1
 * self     ← fp
 * local1   ← fp+1
 * ...
 * localm   ← fp+m 
 *          ← sp
 * </pre>
 * [Frame終了直前]
 * <pre>
 * arg1     ← fp-n
 * ...
 * argn     ← fp-1
 * self     ← fp
 * local1   ← fp+1
 * ...
 * localm   ← fp+m
 * ...
 * ret1
 * ...
 * reto
 *          ← sp
 * </pre>
 * [Frame終了時]
 * <pre>
 * ret1
 * ...
 * reto
 *          ← sp
 * </pre>
 */
public class Frame implements Value {
    
    final int argumentSize, localSize, returnSize;
    final java.util.List<Executable> body;
    final String header;

    Frame(int argumentSize, int localSize, int returnSize, java.util.List<Executable> body,
        java.util.List<Symbol> args, java.util.List<Symbol> rets) {
        this.argumentSize = argumentSize;
        this.localSize = localSize;
        this.returnSize = returnSize;
        this.body = body;
        StringBuilder sb = new StringBuilder();
        for (Symbol s : args)
            sb.append(" ").append(s);
        sb.append(" -");
        for (Symbol s : rets)
            sb.append(" ").append(s);
        this.header = sb.substring(1);
    }

    @Override
    public void call(Context context) {
        var save = new Object() { int oldFp; };
        Executable prolog = c -> {
            // save fp
            save.oldFp = c.fp;
            c.fp = c.stack.size();
            // push self
            c.stack.add(DefinedBody.of(this));
            // initialize locals
            for (int i = 0; i < localSize; ++i)
                c.stack.add(null);
        };
        Executable epilog = c -> {
            // move return values
            int from = c.stack.size() - returnSize;
            int to = c.fp - argumentSize;
            for (int i = 0; i < returnSize; ++i, ++from, ++to)
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(header);
        sb.append(" :");
        for (Executable e : body)
            sb.append(" ").append(e);
        return sb.append("]").toString();
    }
}
