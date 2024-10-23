package saka1029.executable;

public interface Executable {

	void execute(Context c);

	default void call(Context c) {
		execute(c);
	}
}
