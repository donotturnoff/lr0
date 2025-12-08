package net.donotturnoff.lr0;

import java.util.*;

public class Productions {
    Map<NonTerminal, Set<List<Symbol<?>>>> productions;

    public Productions() {
        this.productions = new HashMap<>();
    }

    public Productions(Map<NonTerminal, Set<List<Symbol<?>>>> productions) {
        this.productions = productions;
    }

    public Productions(Set<Production> productions) {
        this.productions = new HashMap<>();
        for (Production production: productions) {
            this.productions.get(production.getHead()).add(production.getBody());
        }
    }

    public Set<Production> get() throws GrammarException {
        Set<Production> ps = new HashSet<>();

        for (NonTerminal head: productions.keySet()) {
            ps.addAll(this.get(head));
        }

        return ps;
    }

    public Set<Production> get(NonTerminal head) throws GrammarException {
        Set<List<Symbol<?>>> bodies = productions.get(head);

        if (bodies.isEmpty()) {
            throw new GrammarException("No productions for head " + head.getName());
        }

        Set<Production> ps = new HashSet<>();
        for (List<Symbol<?>> body: bodies) {
            ps.add(new Production(head, body));
        }

        return ps;
    }

    public void add(NonTerminal head, List<Symbol<?>> body) {
        this.productions.putIfAbsent(head, new HashSet<>());
        this.productions.get(head).add(body);
    }

    public Set<Symbol<?>> getSymbols() {
        Set<Symbol<?>> symbols = new HashSet<>();
        for (NonTerminal head: productions.keySet()) {
            symbols.add(head);
            for (List<Symbol<?>> body: productions.get(head)) {
                symbols.addAll(body);
            }
        }
        return symbols;
    }
}
