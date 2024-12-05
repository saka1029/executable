package saka1029.executable;

public class FrameOffset {
    public final Frame frame;
    public final int offset;

    FrameOffset(Frame frame, int offset) {
        this.frame = frame;
        this.offset = offset;
    }

    public static FrameOffset of(Frame frame, int offset) {
        return new FrameOffset(frame, offset);
    }

    @Override
    public String toString() {
        return "FrameOffset(%s, %d)".formatted(frame, offset);
    }
}
