package saka1029.executable;

public class FrameOffset {

    public final DefineType type;
    public final Frame frame;
    public final int offset;

    FrameOffset(DefineType type, Frame frame, int offset) {
        this.frame = frame;
        this.offset = offset;
        this.type = type;
    }

    public static FrameOffset of(DefineType type, Frame frame, int offset) {
        return new FrameOffset(type, frame, offset);
    }

    @Override
    public String toString() {
        return "FrameOffset(%s, %s, %d)".formatted(type, frame, offset);
    }
}
