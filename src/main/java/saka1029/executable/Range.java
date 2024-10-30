package saka1029.executable;

import java.util.Iterator;
import java.util.Objects;

public class Range implements List {

    public final int start, end, step;

    Range(int start, int end, int step) {
        if (step == 0)
            throw new IllegalArgumentException("step == 0");
        this.start = start;
        this.end = end;
        this.step = step;
    }

    public static Range of(int start, int end, int step) {
        return new Range(start, end, step);
    }

    public static Range of(int start, int end) {
        return new Range(start, end, start <= end ? 1 : -1);
    }

    public static Range of(int end) {
        return new Range(1, end, 1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, step);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Range r
            && r.start == start && r.end == end && r.step == step;
    }

    @Override
    public Iterator<Executable> iterator() {
        return new Iterator<>() {

            int current = start;

            @Override
            public boolean hasNext() {
                return step > 0 && current <= end
                    || step < 0 && current >= end;
            }

            @Override
            public Executable next() {
                int result = current;
                current += step;
                return Int.of(result);
            }
        };
    }

    @Override
    public String toString() {
        return "range(%d, %d, %d)".formatted(start, end, step);
    }
}
