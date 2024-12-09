package saka1029.executable;

public enum DefineType {
    FUNCTION("function"),
    VARIABLE("variable");

    private final String name;

    private DefineType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
