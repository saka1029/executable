package saka1029.executable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Frame implements Value {
    
    final Frame parent;
    final int argumentSize, returnSize;
    final Map<Symbol, LocalValue> locals = new HashMap<>();
    int localSize = 0;
    final java.util.List<Executable> body = new ArrayList<>();
    final String header;

    Frame(Frame parent, java.util.List<Symbol> arguments, int returnSize, String header) {
        this.parent = parent;
        for (int i = arguments.size() - 1, offset = -1; i >= 0; --i, --offset)
            locals.put(arguments.get(i), LocalValue.of(DefineType.VARIABLE, offset));
        this.argumentSize = arguments.size();
        this.returnSize = returnSize;
        this.header = header;
    }

    public static Frame of(Frame parent, java.util.List<Symbol> arguments, int returnSize, String header) {
        return new Frame(parent, arguments, returnSize, header);
    }

    public int addLocal(Symbol name, DefineType type) {
        int offset = localSize++;
        locals.put(name, LocalValue.of(type, offset));
        return offset;
    }

    public static FrameOffset find(Frame frame, Symbol name) {
        for (Frame f = frame; f != null; f = f.parent) {
            LocalValue value = f.locals.get(name);
            if (value != null)
                return FrameOffset.of(value.type, f, value.offset);
        }
        return null;
    }

    /**
     * eplogのスタック操作
     * <pre>
     * [epilog前]
     *          fp                    sp
     * A1 A2 A3 L1 L2 L3 ... R1 R2 R3 -
     * ^to                   ^from
     * </pre>
     * 
     * <pre>
     * [epilog後]
     *          sp
     * R1 R2 R3 -
     * </pre>
     */
    @Override
    public void execute(Context context) {
        Executable prolog = c -> {
            // save fp
            c.pushFp(this);
            // allocate locals
            for (int i = 0; i < localSize; ++i)
                c.stack.add(List.NIL);
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
        context.execute(prolog, c -> c.executables.addLast(body.iterator()), epilog);
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
