package saka1029.executable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParseContext {

    static class Vars {
        final Map<Symbol, Integer> offsets = new LinkedHashMap<>();
    }
    final java.util.List<Vars> frames = new ArrayList<>();

    void beginFrame(java.util.List<Vars> arguments) {

    }

    Frame endFrame() {
        return null;
    }
}
