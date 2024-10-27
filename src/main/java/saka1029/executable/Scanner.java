package saka1029.executable;

public class Scanner {

    final String input;
    final StringBuilder text = new StringBuilder();

    Scanner(String input) {
        this.input = input;
    }

    public static Scanner of(String input) {
        return new Scanner(input);
    }

    public TokenType get() {
        text.setLength(0);
    }

}
