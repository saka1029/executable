package saka1029.executable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParseContext {

    static class Vars {

        final Map<Symbol, Integer> offsets;
        final int returns;
        final java.util.List<Executable> executables = new ArrayList<>();

        Vars(Map<Symbol, Integer> offsets, int returns) {
            this.offsets = offsets;
            this.returns = returns;
        }
    }
    final java.util.List<Vars> frames = new ArrayList<>();

    void beginFrame(java.util.List<Symbol> arguments, int returns) {
        Map<Symbol, Integer> offsets = new HashMap<>();
        for (int i = arguments.size() - 1, offset = -1; i >= 0; --i, --offset)
            offsets.put(arguments.get(i), offset);
        Vars vars = new Vars(offsets, returns);
        frames.add(vars);
    }

    Frame endFrame() {
        Vars vars = frames.remove(frames.size() - 1);
        return new Frame(vars.offsets.size(), vars.returns, Cons.list(vars.executables));
    }
}
