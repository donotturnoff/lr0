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

    public Terminal<?> getNextToken(Set<Symbol<?>> avoid) {
        if (parent == null) {
            return null;
        }

        List<Node> siblings = parent.getChildren();
        List<Node> youngerSiblings = siblings.subList(indexInParent+1, siblings.size());

        for (Node sibling: youngerSiblings) {
            Terminal<?> found = sibling.getFirstToken(avoid);
            if (found != null) {
                return found;
            }
        }

        if (avoid.contains(parent.symbol)) {
            return null;
        }

        return parent.getNextToken(avoid);
    }

    public Terminal<?> getPreviousToken(Set<Symbol<?>> avoid) {
        if (parent == null) {
            return null;
        }

        List<Node> siblings = parent.getChildren();
        List<Node> olderSiblings = siblings.subList(0, indexInParent);

        for (int i = olderSiblings.size()-1; i >= 0; i--) {
            Node sibling = olderSiblings.get(i);
            Terminal<?> found = sibling.getLastToken(avoid);
            if (found != null) {
                return found;
            }
        }

        if (avoid.contains(parent.symbol)) {
            return null;
        }

        return parent.getPreviousToken(avoid);
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

    public Node getNextLeaf(Set<Symbol<?>> avoid) {
        if (parent == null) {
            return null;
        }

        List<Node> siblings = parent.getChildren();
        List<Node> youngerSiblings = siblings.subList(indexInParent+1, siblings.size());

        for (Node sibling: youngerSiblings) {
            Node found = sibling.getFirstLeaf(avoid);
            if (found != null) {
                return found;
            }
        }

        if (avoid.contains(parent.symbol)) {
            return null;
        }

        return parent.getNextLeaf(avoid);
    }

    public Node getPreviousLeaf(Set<Symbol<?>> avoid) {
        if (parent == null) {
            return null;
        }

        List<Node> siblings = parent.getChildren();
        List<Node> olderSiblings = siblings.subList(0, indexInParent);

        for (int i = olderSiblings.size()-1; i >= 0; i--) {
            Node sibling = olderSiblings.get(i);
            Node found = sibling.getLastLeaf(avoid);
            if (found != null) {
                return found;
            }
        }

        if (avoid.contains(parent.symbol)) {
            return null;
        }

        return parent.getPreviousLeaf(avoid);
    }

    public Node getFirstLeaf(Set<Symbol<?>> avoid) {
        if (avoid.contains(symbol)) {
            return null;
        }

        if (children.isEmpty()) {
            return this;
        }

        for (Node child: children) {
            Node n = child.getFirstLeaf(avoid);
            if (n != null) {
                return n;
            }
        }

        return null;
    }

    public Node getLastLeaf(Set<Symbol<?>> avoid) {
        if (avoid.contains(symbol)) {
            return null;
        }

        if (children.isEmpty()) {
            return this;
        }

        ListIterator<Node> childIterator = children.listIterator(children.size());

        while (childIterator.hasPrevious()) {
            Node n = childIterator.previous().getLastLeaf(avoid);
            if (n != null) {
                return n;
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
        if (parent == null) {
            return false;
        }

        List<Node> siblings = parent.children;

        for (int i = indexInParent-1; i >= 0; i--) {
            SearchResult res = siblings.get(i).endsWithAHelper(targets, avoid);
            if (res != SearchResult.AVOIDED) {
                return res == SearchResult.FOUND;
            }
        }

        return parent.isPrecededByA(targets, avoid);
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
        if (parent == null) {
            return false;
        }

        List<Node> siblings = parent.children;

        for (int i = indexInParent+1; i < siblings.size(); i++) {
            SearchResult res = siblings.get(i).startsWithAHelper(targets, avoid);
            if (res != SearchResult.AVOIDED) {
                return res == SearchResult.FOUND;
            }
        }

        return parent.isFollowedByA(targets, avoid);
    }

    public boolean isDescendantOfA(Set<Symbol<?>> targets, Set<Symbol<?>> avoid) {
        if (parent == null || avoid.contains(parent.getSymbol())) {
            return false;
        }

        if (targets.contains(parent.getSymbol())) {
            return true;
        }

        return parent.isDescendantOfA(targets, avoid);
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

    public Symbol<?> findFirstDescendant(Symbol<?> target) {
        return findFirstDescendant(Set.of(target), Set.of());
    }

    public Symbol<?> findFirstDescendant(Set<Symbol<?>> targets) {
        return findFirstDescendant(targets, Set.of());
    }

    public Symbol<?> findFirstDescendant(Symbol<?> target, Symbol<?> avoid) {
        return findFirstDescendant(Set.of(target), Set.of(avoid));
    }

    public Symbol<?> findFirstDescendant(Symbol<?> target, Set<Symbol<?>> avoid) {
        return findFirstDescendant(Set.of(target), avoid);
    }

    public Symbol<?> findFirstDescendant(Set<Symbol<?>> targets, Set<Symbol<?>> avoid) {
        if (avoid.contains(symbol)) {
            return null;
        }

        if (targets.contains(symbol)) {
            return symbol;
        }

        for (Node child: children) {
            Symbol<?> found = child.findFirstDescendant(targets, avoid);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    public Symbol<?> findLastDescendant(Symbol<?> target) {
        return findLastDescendant(Set.of(target), Set.of());
    }

    public Symbol<?> findLastDescendant(Set<Symbol<?>> targets) {
        return findLastDescendant(targets, Set.of());
    }

    public Symbol<?> findLastDescendant(Symbol<?> target, Symbol<?> avoid) {
        return findLastDescendant(Set.of(target), Set.of(avoid));
    }

    public Symbol<?> findLastDescendant(Symbol<?> target, Set<Symbol<?>> avoid) {
        return findLastDescendant(Set.of(target), avoid);
    }

    public Symbol<?> findLastDescendant(Set<Symbol<?>> targets, Set<Symbol<?>> avoid) {
        if (avoid.contains(symbol)) {
            return null;
        }

        if (targets.contains(symbol)) {
            return symbol;
        }

        for (int i = children.size()-1; i >= 0; i--) {
            Node child = children.get(i);
            Symbol<?> found = child.findLastDescendant(targets, avoid);
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
            Symbol<?> found = sibling.findFirstDescendant(targets, avoid);
            if (found != null) {
                return found;
            }
        }

        if (avoid.contains(parent.symbol)) {
            return null;
        }

        return parent.findNextFollowing(targets, avoid);
    }

    public Symbol<?> findPreviousPreceding(Symbol<?> target) {
        return findPreviousPreceding(Set.of(target), Set.of());
    }

    public Symbol<?> findPreviousPreceding(Set<Symbol<?>> targets) {
        return findPreviousPreceding(targets, Set.of());
    }

    public Symbol<?> findPreviousPreceding(Symbol<?> target, Symbol<?> avoid) {
        return findPreviousPreceding(Set.of(target), Set.of(avoid));
    }

    public Symbol<?> findPreviousPreceding(Symbol<?> target, Set<Symbol<?>> avoid) {
        return findPreviousPreceding(Set.of(target), avoid);
    }

    public Symbol<?> findPreviousPreceding(Set<Symbol<?>> targets, Set<Symbol<?>> avoid) {
        if (parent == null) {
            return null;
        }

        List<Node> siblings = parent.getChildren();
        List<Node> olderSiblings = siblings.subList(0, indexInParent);

        for (int i = olderSiblings.size()-1; i >= 0; i--) {
            Node sibling = olderSiblings.get(i);
            Symbol<?> found = sibling.findLastDescendant(targets, avoid);
            if (found != null) {
                return found;
            }
        }

        if (avoid.contains(parent.symbol)) {
            return null;
        }

        return parent.findPreviousPreceding(targets, avoid);
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
            Symbol<?> found = sibling.findFirstDescendant(targets, avoid);
            if (found != null) {
                return found;
            }
        }

        if (avoid.contains(parent.symbol)) {
            return null;
        }

        return parent.findNextFollowing(targets, avoid);
    }

    public List<Node> findAllDescendants(Symbol<?> target) {
        return findAllDescendants(Set.of(target), Set.of(), Set.of());
    }

    public List<Node> findAllDescendants(Set<Symbol<?>> targets) {
        return findAllDescendants(targets, Set.of(), Set.of());
    }

    public List<Node> findAllDescendants(Symbol<?> target, Symbol<?> avoid) {
        return findAllDescendants(Set.of(target), Set.of(avoid), Set.of());
    }

    public List<Node> findAllDescendants(Symbol<?> target, Set<Symbol<?>> avoid) {
        return findAllDescendants(Set.of(target), avoid, Set.of());
    }

    public List<Node> findAllDescendants(Set<Symbol<?>> targets, Set<Symbol<?>> avoid) {
        return findAllDescendants(targets, avoid, Set.of());
    }

    public List<Node> findAllDescendants(Set<Symbol<?>> targets, Set<Symbol<?>> avoid, Set<?> values) {
        if (avoid.contains(symbol)) {
            return List.of();
        }

        List<Node> found = new ArrayList<>();
        if (targets.contains(symbol)) {
            if (values.isEmpty() || (this.getSymbol() instanceof Terminal<?> t && values.contains(t.getValue()))) {
                found.add(this);
            }
        }

        for (Node child: children) {
            found.addAll(child.findAllDescendants(targets, avoid, values));
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
