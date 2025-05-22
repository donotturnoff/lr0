package net.donotturnoff.lr0;

import java.util.Set;

public class Frame {
    private final Set<Item> state;
    private final Node node;
    
    public Frame(Set<Item> state, Node node) {
        this.state = state;
        this.node = node;
    }
    
    public Set<Item> getState() {
        return state;
    }
    
    public Node getNode() {
        return node;
    }
    
    public String toString() {
        return "Frame[state=" + state + ", node=" + node + "]";
    }
}
