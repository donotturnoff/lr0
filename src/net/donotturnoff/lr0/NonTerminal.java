package net.donotturnoff.lr0;

import java.util.HashMap;

public class NonTerminal extends Symbol<Void> {
    
    protected NonTerminal() {
        this.name = null;
    }
    
    public NonTerminal(String name) {
        this.name = name.toLowerCase();
    }
}
