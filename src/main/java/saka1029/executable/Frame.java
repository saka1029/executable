package saka1029.executable;

import java.util.Iterator;

/**
 * 
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

    Frame(int argumentSize, int localSize, int returnSize, java.util.List<Executable> body, String header) {
        this.argumentSize = argumentSize;
        this.localSize = localSize;
        this.returnSize = returnSize;
        this.body = body;
        this.header = header;
    }

    @Override
    public void execute(Context context) {
        Executable prolog = c -> {
            c.pushFp(this);
            // push self
            c.stack.add(this);
            // initialize locals
            for (int i = 0; i < localSize; ++i)
                c.stack.add(null);
        };
        Executable epilog = c -> {
            // move return values
            int from = c.stack.size() - returnSize;
            int to = c.fp(this) - argumentSize;
            for (int i = 0; i < returnSize; ++i, ++from, ++to)
                c.stack.set(to, c.stack.get(from));
            // drop stack
            while (c.stack.size() > to)
                c.stack.remove(c.stack.size() - 1);
            // restore fp
            c.popFp(this);
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

    /**
     * hashCodeとequalsは定義しないこと。
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * hashCodeとequalsは定義しないこと。
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(header);
        for (Executable e : body)
            sb.append(" ").append(e);
        return sb.append("]").toString();
    }
}
