package saka1029.executable;

public class Frame implements Executable {
    
    final int arguments, returns;
    final List executables;

    Frame(int arguments, int returns, List executables) {
        this.arguments = arguments;
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
