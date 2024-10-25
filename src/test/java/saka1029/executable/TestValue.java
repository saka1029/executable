package saka1029.executable;

import org.junit.Test;

import saka1029.Common;

import static saka1029.executable.Helper.*;

import java.util.logging.Logger;

public class TestValue {

    static final Logger logger = Common.logger(TestValue.class);

    static final Context c = Context.of();

    @Test
    public void testInt() {
        List list = l(i(1), i(2), s("+"));
        c.run(list);
        System.out.println(c);
    }
}
