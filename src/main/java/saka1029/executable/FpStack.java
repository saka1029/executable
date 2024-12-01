package saka1029.executable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class FpStack {

    record FrameFp(Frame f, int fp) {}
    final Deque<FrameFp> stack = new ArrayDeque<>();
    final Map<Frame, Deque<Integer>> map = new HashMap<>();

    public void push(Frame f, int fp) {
        FrameFp ff = new FrameFp(f, fp);
        stack.add(ff);
        map.computeIfAbsent(f, k -> new ArrayDeque<>()).add(fp);
    }

    public void pop() {
        FrameFp ff = stack.removeLast();
        if (!map.containsKey(ff.f))
            throw new RuntimeException("Frame '%s' is not defined in map".formatted(ff.f));
        map.get(ff.f).removeLast();
    }

    public int fp(Frame f) {
        Deque<Integer> que = map.get(f);
        if (que == null)
            throw new RuntimeException("Frame '%s' is not defined in map".formatted(f));
        return que.getLast();
    }
}
