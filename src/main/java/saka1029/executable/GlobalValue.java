package saka1029.executable;

public class GlobalValue {

    final Executable value;
    final DefineType type;

    GlobalValue(Executable value, DefineType type) {
        this.value = value;
        this.type = type;
    }

    static GlobalValue of(Executable value, DefineType type) {
        return new GlobalValue(value, type);
    }

    @Override
    public String toString() {
        return "GlobalValue(%s, %s)".formatted(value, type);
    }
}
