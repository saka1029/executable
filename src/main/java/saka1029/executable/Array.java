package saka1029.executable;

import java.util.Arrays;
import java.util.Iterator;

public class Array implements Executable, Iterable<Executable> {

    final Executable[] array;

    Array(int size) {
        this.array = new Executable[size];
    }

    Array(int size, Executable fill) {
        this(size);
        Arrays.fill(array, fill);
    }

    public int size() {
        return array.length;
    }

    public Executable get(int index) {
        return array[index];
    }

    public void put(int index, Executable value) {
        array[index] = value;
    }

    public static Array of(int size, Executable fill) {
        return new Array(size, fill);
    }

    public static Array of(Executable... es) {
        int size = es.length;
        Array a = new Array(size);
        System.arraycopy(es, 0, a.array, 0, size);
        return a;
    }

    @Override
    public Iterator<Executable> iterator() {
        return new Iterator<>() {

            int index = 0;

            @Override
            public boolean hasNext() {
                return index < array.length;
            }

            @Override
            public Executable next() {
                return array[index++];
            }
        };
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Array right && Arrays.equals(array, right.array);
    }

    @Override
    public void execute(Context c) {
        c.executables.addLast(iterator());
    }

    @Override
    public String toString() {
        return Arrays.toString(array);
    }
}
