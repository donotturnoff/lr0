package net.donotturnoff.lr0;

public class Token<T> {
    private final T value;
    private final int line, column;

    public Token(int line, int column) {
        this(line, column, null);
    }

    public Token(int line, int column, T value) {
        this.line = line;
        this.column = column;
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
