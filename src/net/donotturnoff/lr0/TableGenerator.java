package net.donotturnoff.lr0;

import java.util.Set;
import java.util.HashSet;

public class TableGenerator {
    private Set<Set<Item>> states;
    private final Grammar g;
    private Set<Item> start;
    
    public TableGenerator(Grammar g) {
        this.states = new HashSet<>();
        this.g = g;
        constructStates();
    }
    
    public Set<Item> closure(Set<Item> s) {
        Set<Item> closure = new HashSet<>(s);
        
        boolean altered; 
        do {
            altered = false;
            Set<Item> newClosure = new HashSet<>(closure);
            for (Item i: closure) {
                Symbol<?> b = i.getNextSymbol();
                for (Production p: g.getProductions()) {
                    if (p.getHead().equals(b)) {
                        altered = altered || newClosure.add(new Item(p, 0));
                    }
                }
            }
            closure = newClosure;
        } while (altered);
        
        return closure;   
    }
    
    public Set<Item> goTo(Set<Item> s, Symbol<?> x) {
        Set<Item> next = new HashSet<>();
        for (Item i: s) {
            Symbol<?> nextSymbol = i.getNextSymbol();
            if ((nextSymbol != null) && (nextSymbol.equals(x))) {
                next.add(new Item(i.getProduction(), i.getIndex()+1));
            }
        }
        return closure(next);
    }
    
    private void constructStates() {
        Item seedItem = new Item(g.getStartProduction(), 0);
        start = new HashSet<>();
        start.add(seedItem);
        start = closure(start);
        states.add(start);
        boolean altered;
        do {
            altered = false;
            Set<Set<Item>> newStates = new HashSet<>(states);
            for (Set<Item> s: states) {
                for (Symbol<?> x: g.getSymbols()) {
                    Set<Item> nextState = goTo(s, x);
                    if (nextState != null) {
                        altered = altered || newStates.add(nextState);
                    }
                }
            }
            states = newStates;
        } while (altered);
    }
    
    public Set<Item> getStart() {
        return start;
    }
    
    public Table<Set<Item>, NonTerminal, Set<Item>> getGoTo() {
        Table<Set<Item>, NonTerminal, Set<Item>> goTo = new Table<>();
        
        for (Set<Item> s: states) {
            for (Symbol<?> x: g.getSymbols()) {
                if (x instanceof NonTerminal) {
                    Set<Item> nextState = goTo(s, x);
                    if (nextState != null) {
                        goTo.put(s, (NonTerminal) x, nextState);
                    }
                }
            }
        }
        
        return goTo;
    }
     
    public Table<Set<Item>, Terminal<?>, Action<?>> getAction() {
        Table<Set<Item>, Terminal<?>, Action<?>> action = new Table<>();

        for (Set<Item> s: states) {
            for (Item i: s) {
                if (!i.isAtEnd()) {
                    Symbol<?> nextSymbol = i.getNextSymbol();
                    Set<Item> nextState = goTo(s, nextSymbol);
                    if (nextState != null && nextSymbol instanceof Terminal) {
                        action.put(s, (Terminal<?>) nextSymbol, new Action<>(Action.SHIFT, nextState));
                    }
                } else if (i.getHead() instanceof AugmentedStartSymbol && i.isAtEnd()) {
                    action.put(s, new EOF(), new Action<>(Action.ACCEPT));
                } else if (!(i.getHead() instanceof AugmentedStartSymbol) && i.isAtEnd()) {
                    for (Terminal<?> a: g.follow(i.getHead())) {
                        action.put(s, a, new Action<>(Action.REDUCE, i.getProduction()));
                    }
                }
            }
        }
        return action;
    }
}
