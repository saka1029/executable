package saka1029.executable;

public interface Executable {
	default void execute(Context c) {
		c.push(this);
	}
	default void call(Context c) {
		execute(c);
	}
}
