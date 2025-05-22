package net.donotturnoff.lr0;

public class Range {
    private final int start, end;

    public Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public Range(Range start, Range end) {
        if (start == null && end == null) {
            this.start = -1;
            this.end = -1;
        } else if (start == null) {
            this.start = end.start;
            this.end = end.end;
        } else if (end == null) {
            this.start = start.start;
            this.end = start.end;
        } else {
            this.start = start.start;
            this.end = end.end;
        }
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return start + "-" + end;
    }
}