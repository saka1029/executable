package saka1029.executable;

public class FunctionVariable {

    Executable value;
    boolean isFunction;

    FunctionVariable(Executable value, boolean isFunction) {
        this.value = value;
        this.isFunction = isFunction;
    }

    static FunctionVariable of(Executable value, boolean isFunction) {
        return new FunctionVariable(value, isFunction);
    }

    @Override
    public String toString() {
        return "FunctionVariable(%s, %s)".formatted(value, isFunction);
    }
}
