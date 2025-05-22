package net.donotturnoff.lr0;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Grammar {
    private final Set<Symbol<?>> symbols;
    private final Productions productions;
    private Map<Symbol<?>, Set<Terminal<?>>> first;
    private Map<NonTerminal, Set<Terminal<?>>> follow;

    public Grammar(Productions productions, NonTerminal originalStart) {
        this.productions = productions;
        this.symbols = productions.getSymbols();
        computeFirst();
        computeFollow(originalStart);
        productions.add(new AugmentedStartSymbol(), List.of(originalStart));
    }
    
    private void computeFirst() {
        first = new HashMap<>();        
        first.put(new Epsilon(), Set.of());
        
        for (Symbol<?> s: symbols) {
            if (s instanceof Terminal) {
                first.put(s, Set.of((Terminal<?>) s));
            } else if (s instanceof NonTerminal) {
                first.put(s, new HashSet<>());
            }
        }
        
        boolean altered;
        do {
            altered = false;
            for (Symbol<?> s: symbols) {
                if (s instanceof NonTerminal) {
                    Set<Production> ps = getProductions((NonTerminal) s);
                    for (Production p: ps) {
                        List<Symbol<?>> body = p.getBody().stream().filter(x -> !(x instanceof Epsilon)).toList();
                        Set<Terminal<?>> newFirst = new HashSet<>(first.get(s));
                        if (body.isEmpty()) {
                            altered = altered || newFirst.add(new Epsilon());
                        } else {
                            altered = altered || newFirst.addAll(first.get(body.get(0)));
                        }
                        if (altered) {
                            first.put(s, newFirst);
                        }
                    }
                }
            }
            
        } while (altered);
    }
    
    private void computeFollow(NonTerminal start) {
        follow = new HashMap<>();        
        
        for (Symbol<?> s: symbols) {
            if (s instanceof NonTerminal) {
                follow.put((NonTerminal) s, new HashSet<>());
            }
        }
        follow.put(start, Set.of(new EOF()));
        
        boolean altered;
        do {
            Map<NonTerminal, Set<Terminal<?>>> newFollow = new HashMap<>(follow);
            for (Production p: productions.get()) {
                List<Symbol<?>> body = p.getBody();
                for (int i = 0; i < body.size()-1; i++) {
                    Symbol<?> s = body.get(i);
                    Symbol<?> next = body.get(i+1);
                    if (s instanceof NonTerminal && !s.equals(start)) {
                        Set<Terminal<?>> f = first.get(next);
                        if (f.contains(new Epsilon())) {
                            Set<Terminal<?>> currentFollowing = new HashSet<>(newFollow.get(s));
                            currentFollowing.addAll(newFollow.get(p.getHead()));
                            newFollow.put((NonTerminal) s, currentFollowing);
                        }
                        Set<Terminal<?>> currentFollowing = new HashSet<>(newFollow.get(s));
                        currentFollowing.addAll(f.stream().filter(x -> !(x instanceof Epsilon)).collect(Collectors.toSet()));
                        newFollow.put((NonTerminal) s, currentFollowing);
                    }
                }
                Symbol<?> s = body.get(body.size()-1);
                if (s instanceof NonTerminal) {
                    Set<Terminal<?>> followingHead = new HashSet<>(newFollow.get(p.getHead()));
                    Set<Terminal<?>> currentFollowing = new HashSet<>(newFollow.get(s));
                    currentFollowing.addAll(followingHead);
                    newFollow.put((NonTerminal) s, currentFollowing);
                }
            }
            altered = !(newFollow.equals(follow));
            follow = newFollow;
        } while (altered);
    }
    
    public Production getStartProduction() {
        return List.copyOf(productions.get(new AugmentedStartSymbol())).get(0);
    }
    
    public Set<Symbol<?>> getSymbols() {
        return symbols;
    }

    public Set<Production> getProductions(NonTerminal head) {
        return productions.get(head);
    }
    
    public Set<Terminal<?>> first(Symbol<?> s) {
        return first.get(s);
    }
    
    public Set<Terminal<?>> follow(NonTerminal s) {
        return follow.get(s);
    }
}
