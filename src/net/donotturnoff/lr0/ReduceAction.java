package net.donotturnoff.lr0;

public class ReduceAction extends Action {
    private final Production p;

    public ReduceAction(Production p) {
        this.p = p;
    }

    public Production getProduction() {
        return p;
    }

    @Override
    public String toString() {
        return "REDUCE(" + p + ")";
    }
}
