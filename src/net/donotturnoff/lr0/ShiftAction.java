package net.donotturnoff.lr0;

import java.util.Set;

public class ShiftAction extends Action {
    private final Set<Item> nextState;

    public ShiftAction(Set<Item> nextState) {
        this.nextState = nextState;
    }

    public Set<Item> getNextState() {
        return nextState;
    }

    @Override
    public String toString() {
        return "SHIFT(" + nextState + ")";
    }
}
