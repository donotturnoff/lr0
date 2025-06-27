package net.donotturnoff.lr0;

import java.util.*;

public class Node {
    private Node parent;
    private int indexInParent;
    private final List<Node> children;
    private final Symbol<?> symbol;
    
    public Node(Symbol<?> symbol) {
        this.children = new ArrayList<>();
        this.symbol = symbol;
        this.symbol.setNode(this);
    }
    
    public Node(Symbol<?> symbol, List<Node> children) {
        this.children = children;
        this.symbol = symbol;
        this.symbol.setNode(this);
        int i = 0;
        for (Node child: children) {
            child.parent = this;
            child.indexInParent = i++;
        }
    }
    
    public void addChild(Node child) {
        children.add(child);
        child.parent = this;
        child.indexInParent = children.size()-1;
    }
    
    public List<Node> getChildren() {
        return children;
    }

    public Node getParent() { return parent; }
    
    public Symbol<?> getSymbol() {
        return symbol;
    }

    public Terminal<?> getFirstToken(Set<Symbol<?>> avoid) {
        if (avoid.contains(symbol)) {
            return null;
        }

        if (symbol instanceof Terminal<?> t) {
            return t;
        }

        for (Node child: children) {
            Terminal<?> t = child.getFirstToken(avoid);
            if (t != null) {
                return t;
            }
        }

        return null;
    }

    public Terminal<?> getLastToken(Set<Symbol<?>> avoid) {
        if (avoid.contains(symbol)) {
            return null;
        }

        if (symbol instanceof Terminal<?> t) {
            return t;
        }

        ListIterator<Node> childIterator = children.listIterator(children.size());

        while (childIterator.hasPrevious()) {
            Terminal<?> t = childIterator.previous().getLastToken(avoid);
            if (t != null) {
                return t;
            }
        }

        return null;
    }

    public boolean isPrecededByA(Symbol<?> target) {
        return isPrecededByA(Set.of(target), Set.of());
    }

    public boolean isPrecededByA(Set<Symbol<?>> targets) {
        return isPrecededByA(targets, Set.of());
    }

    public boolean isPrecededByA(Symbol<?> target, Symbol<?> avoid) {
        return isPrecededByA(Set.of(target), Set.of(avoid));
    }

    public boolean isPrecededByA(Symbol<?> target, Set<Symbol<?>> avoid) {
        return isPrecededByA(Set.of(target), avoid);
    }

    public boolean isPrecededByA(Set<Symbol<?>> targets, Set<Symbol<?>> avoid) {
        List<Node> siblings = parent.children;
        if (indexInParent == 0) {
            return parent.isPrecededByA(targets, avoid);
        }

        SearchResult res = siblings.get(indexInParent-1).endsWithAHelper(targets, avoid);
        if (res == SearchResult.AVOIDED) {
            return parent.isPrecededByA(targets, avoid);
        } else {
            return res == SearchResult.FOUND;
        }
    }

    public boolean isFollowedByA(Symbol<?> target) {
        return isFollowedByA(Set.of(target), Set.of());
    }

    public boolean isFollowedByA(Set<Symbol<?>> targets) {
        return isFollowedByA(targets, Set.of());
    }

    public boolean isFollowedByA(Symbol<?> target, Symbol<?> avoid) {
        return isFollowedByA(Set.of(target), Set.of(avoid));
    }

    public boolean isFollowedByA(Symbol<?> target, Set<Symbol<?>> avoid) {
        return isFollowedByA(Set.of(target), avoid);
    }

    public boolean isFollowedByA(Set<Symbol<?>> targets, Set<Symbol<?>> avoid) {
        List<Node> siblings = parent.children;
        if (indexInParent >= siblings.size()-1) {
            return parent.isFollowedByA(targets, avoid);
        }

        SearchResult res = siblings.get(indexInParent+1).startsWithAHelper(targets, avoid);
        if (res == SearchResult.AVOIDED) {
            return parent.isFollowedByA(targets, avoid);
        } else {
            return res == SearchResult.FOUND;
        }
    }

    public boolean startsWithA(Symbol<?> target) {
        return startsWithA(Set.of(target), Set.of());
    }

    public boolean startsWithA(Set<Symbol<?>> targets) {
        return startsWithA(targets, Set.of());
    }

    public boolean startsWithA(Symbol<?> target, Symbol<?> avoid) {
        return startsWithA(Set.of(target), Set.of(avoid));
    }

    public boolean startsWithA(Symbol<?> target, Set<Symbol<?>> avoid) {
        return startsWithA(Set.of(target), avoid);
    }

    public boolean startsWithA(Set<Symbol<?>> targets, Set<Symbol<?>> avoid) {
        SearchResult res = startsWithAHelper(targets, avoid);
        return res == SearchResult.FOUND;
    }

    private enum SearchResult {
        FOUND, NOT_FOUND, AVOIDED
    }

    private SearchResult startsWithAHelper(Set<Symbol<?>> targets, Set<Symbol<?>> avoid) {
        if (avoid.contains(symbol)) {
            return SearchResult.AVOIDED;
        } else if (targets.contains(symbol)) {
            return SearchResult.FOUND;
        }

        if (children.isEmpty()) {
            return SearchResult.NOT_FOUND;
        }

        int i = 0;
        while (i < children.size()) {
            SearchResult res = children.get(i).startsWithAHelper(targets, avoid);
            if (Set.of(SearchResult.FOUND, SearchResult.NOT_FOUND).contains(res)) {
                return res;
            }
            i++;
        }

        return SearchResult.AVOIDED;
    }

    public boolean endsWithA(Symbol<?> target) {
        return endsWithA(Set.of(target), Set.of());
    }

    public boolean endsWithA(Set<Symbol<?>> targets) {
        return endsWithA(targets, Set.of());
    }

    public boolean endsWithA(Symbol<?> target, Symbol<?> avoid) {
        return endsWithA(Set.of(target), Set.of(avoid));
    }

    public boolean endsWithA(Symbol<?> target, Set<Symbol<?>> avoid) {
        return endsWithA(Set.of(target), avoid);
    }

    public boolean endsWithA(Set<Symbol<?>> targets, Set<Symbol<?>> avoid) {
        SearchResult res = endsWithAHelper(targets, avoid);
        return res == SearchResult.FOUND;
    }

    private SearchResult endsWithAHelper(Set<Symbol<?>> targets, Set<Symbol<?>> avoid) {
        if (avoid.contains(symbol)) {
            return SearchResult.AVOIDED;
        } else if (targets.contains(symbol)) {
            return SearchResult.FOUND;
        }

        if (children.isEmpty()) {
            return SearchResult.NOT_FOUND;
        }

        int i = children.size()-1;
        while (i >= 0) {
            SearchResult res = children.get(i).endsWithAHelper(targets, avoid);
            if (Set.of(SearchResult.FOUND, SearchResult.NOT_FOUND).contains(res)) {
                return res;
            }
            i--;
        }

        return SearchResult.AVOIDED;
    }

    public Symbol<?> findNextDescendant(Symbol<?> target) {
        return findNextDescendant(Set.of(target), Set.of());
    }

    public Symbol<?> findNextDescendant(Set<Symbol<?>> targets) {
        return findNextDescendant(targets, Set.of());
    }

    public Symbol<?> findNextDescendant(Symbol<?> target, Symbol<?> avoid) {
        return findNextDescendant(Set.of(target), Set.of(avoid));
    }

    public Symbol<?> findNextDescendant(Symbol<?> target, Set<Symbol<?>> avoid) {
        return findNextDescendant(Set.of(target), avoid);
    }

    public Symbol<?> findNextDescendant(Set<Symbol<?>> targets, Set<Symbol<?>> avoid) {
        if (avoid.contains(symbol)) {
            return null;
        }

        if (targets.contains(symbol)) {
            return symbol;
        }

        for (Node child: children) {
            Symbol<?> found = child.findNextDescendant(targets, avoid);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    public Symbol<?> findNextFollowing(Symbol<?> target) {
        return findNextFollowing(Set.of(target), Set.of());
    }

    public Symbol<?> findNextFollowing(Set<Symbol<?>> targets) {
        return findNextFollowing(targets, Set.of());
    }

    public Symbol<?> findNextFollowing(Symbol<?> target, Symbol<?> avoid) {
        return findNextFollowing(Set.of(target), Set.of(avoid));
    }

    public Symbol<?> findNextFollowing(Symbol<?> target, Set<Symbol<?>> avoid) {
        return findNextFollowing(Set.of(target), avoid);
    }

    public Symbol<?> findNextFollowing(Set<Symbol<?>> targets, Set<Symbol<?>> avoid) {
        if (parent == null) {
            return null;
        }

        List<Node> siblings = parent.getChildren();
        List<Node> youngerSiblings = siblings.subList(indexInParent+1, siblings.size());

        for (Node sibling: youngerSiblings) {
            Symbol<?> found = sibling.findNextDescendant(targets, avoid);
            if (found != null) {
                return found;
            }
        }

        if (avoid.contains(parent.symbol)) {
            return null;
        }

        return parent.findNextFollowing(targets, avoid);
    }

    public Symbol<?> findNext(Symbol<?> target) {
        return findNext(Set.of(target), Set.of());
    }

    public Symbol<?> findNext(Set<Symbol<?>> targets) {
        return findNext(targets, Set.of());
    }

    public Symbol<?> findNext(Symbol<?> target, Symbol<?> avoid) {
        return findNext(Set.of(target), Set.of(avoid));
    }

    public Symbol<?> findNext(Symbol<?> target, Set<Symbol<?>> avoid) {
        return findNext(Set.of(target), avoid);
    }

    public Symbol<?> findNext(Set<Symbol<?>> targets, Set<Symbol<?>> avoid) {
        if (parent == null) {
            return null;
        }

        List<Node> siblings = parent.getChildren();
        List<Node> youngerSiblings = siblings.subList(indexInParent, siblings.size());

        for (Node sibling: youngerSiblings) {
            Symbol<?> found = sibling.findNextDescendant(targets, avoid);
            if (found != null) {
                return found;
            }
        }

        if (avoid.contains(parent.symbol)) {
            return null;
        }

        return parent.findNextFollowing(targets, avoid);

    }

    public Set<Node> findAllDescendants(Symbol<?> target) {
        return findAllDescendants(Set.of(target), Set.of());
    }

    public Set<Node> findAllDescendants(Set<Symbol<?>> targets) {
        return findAllDescendants(targets, Set.of());
    }

    public Set<Node> findAllDescendants(Symbol<?> target, Symbol<?> avoid) {
        return findAllDescendants(Set.of(target), Set.of(avoid));
    }

    public Set<Node> findAllDescendants(Symbol<?> target, Set<Symbol<?>> avoid) {
        return findAllDescendants(Set.of(target), avoid);
    }

    public Set<Node> findAllDescendants(Set<Symbol<?>> targets, Set<Symbol<?>> avoid) {
        if (avoid.contains(symbol)) {
            return Set.of();
        }

        Set<Node> found = new HashSet<>();
        if (targets.contains(symbol)) {
            found.add(this);
        }

        for (Node child: children) {
            found.addAll(child.findAllDescendants(targets, avoid));
        }

        return found;
    }
    
    @Override
    public String toString() {
        return toString("");
    }
    
    private String toString(String indent) {
        if (children.isEmpty()) {
            return indent + symbol.toString();
        }
        
        StringBuilder s = new StringBuilder(indent + symbol.toString() + "{\n");
        for (Node child: children) {
            s.append(child.toString(indent + " ")).append("\n");
        }
        s.append(indent).append("}");
        return s.toString();
    }
}
