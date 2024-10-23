package saka1029.executable;

public interface Value {
	@Override
	void execute(Context c) {
		c.push(this);
	}
}
