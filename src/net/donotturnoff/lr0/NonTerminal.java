package net.donotturnoff.lr0;

public class NonTerminal extends Symbol<Void> {
    
    protected NonTerminal() {}
    
    public NonTerminal(String name) {
        this.name = name.toLowerCase();
    }
}
