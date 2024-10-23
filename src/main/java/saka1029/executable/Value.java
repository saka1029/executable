package saka1029.executable;

public interface Value extends Executable {

	@Override
	default void execute(Context c) {
		c.push(this);
	}
}
