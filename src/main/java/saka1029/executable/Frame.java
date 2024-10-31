package saka1029.executable;

public class Frame {
    
    final int arguments, returns;
    final List executables;

    Frame(int arguments, int returns, List executables) {
        this.arguments = arguments;
        this.returns = returns;
        this.executables = executables;
    }

}
