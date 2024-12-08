package saka1029.executable;

public class LocalValue {

    final DefineType type;
    final int offset;

    LocalValue(DefineType type, int offset) {
        this.type = type;
        this.offset = offset;
    }

    public static LocalValue of(DefineType type, int offset) {
        return new LocalValue(type, offset);
    }

    @Override
    public String toString() {
        return "LocalValue(%s, %d)".formatted(type, offset);
    }
}
