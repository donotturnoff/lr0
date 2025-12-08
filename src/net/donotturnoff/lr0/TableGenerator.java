package net.donotturnoff.lr0;

import java.io.FileInputStream;
import java.util.Set;
import java.util.HashSet;

public class TableGenerator {
    private Set<Set<Item>> states;
    private final Grammar g;
    private Set<Item> start;
    
    public TableGenerator(Grammar g) throws GrammarException {
        this.states = new HashSet<>();
        this.g = g;
        constructStates();
    }
    
    private Set<Item> closure(Set<Item> s) throws GrammarException {
        Set<Item> closure = new HashSet<>(s);
        
        boolean altered; 
        do {
            altered = false;
            Set<Item> newClosure = new HashSet<>(closure);
            for (Item i: closure) {
                Symbol<?> b = i.getNextSymbol();
                if (b instanceof NonTerminal) {
                    for (Production p : g.getProductions((NonTerminal) b)) {
                        altered = altered || newClosure.add(new Item(p, 0));
                    }
                }
            }
            closure = newClosure;
        } while (altered);
        
        return closure;
    }
    
    private Set<Item> goTo(Set<Item> s, Symbol<?> x) throws GrammarException {
        Set<Item> next = new HashSet<>();
        for (Item i: s) {
            Symbol<?> nextSymbol = i.getNextSymbol();
            if ((nextSymbol != null) && (nextSymbol.equals(x))) {
                next.add(new Item(i.getProduction(), i.getIndex()+1));
            }
        }
        return closure(next);
    }
    
    private void constructStates() throws GrammarException {
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
    
    public Table<Set<Item>, NonTerminal, Set<Item>> getGoTo() throws GrammarException {
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
     
    public Table<Set<Item>, Terminal<?>, Action> getAction() throws GrammarException {
        Table<Set<Item>, Terminal<?>, Action> action = new Table<>();

        for (Set<Item> s: states) {
            for (Item i: s) {
                if (!i.isAtEnd()) {
                    Symbol<?> nextSymbol = i.getNextSymbol();
                    Set<Item> nextState = goTo(s, nextSymbol);
                    if (nextState != null && nextSymbol instanceof Terminal) {
                        action.put(s, (Terminal<?>) nextSymbol, new ShiftAction(nextState));
                    }
                } else if (i.getHead() instanceof AugmentedStartSymbol && i.isAtEnd()) {
                    action.put(s, new EOF(), new AcceptAction());
                } else if (!(i.getHead() instanceof AugmentedStartSymbol) && i.isAtEnd()) {
                    for (Terminal<?> a: g.follow(i.getHead())) {
                        action.put(s, a, new ReduceAction(i.getProduction()));
                    }
                }
            }
        }
        return action;
    }
}
