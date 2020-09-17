package net.donotturnoff.lr0;

public class Terminal<T> extends Symbol<T> {

    protected Token<T> t;
    
    protected Terminal() {}
    
    public Terminal(String name) {
        this.name = name;
        this.t = null;
    }

    public Terminal(String name, int line, int column) {
        this.name = name;
        this.t = new Token<>(line, column);
    }

    public Terminal(String name, int line, int column, T value) {
        this.name = name.toUpperCase();
        this.t = new Token<>(line, column, value);
    }

    public Token<T> getToken() {
        return t;
    }

    @Override
    public String toString() {
        if (t == null || t.getValue() == null) {
            return name;
        } else {
            return name + "(" + t.getValue() + ")";
        }
    }
}
