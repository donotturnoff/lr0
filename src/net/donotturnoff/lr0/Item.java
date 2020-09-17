package net.donotturnoff.lr0;

import java.util.Objects;

public class Item {
    private final Production p;
    private final int index;
    
    public Item(Production p, int index) throws IndexOutOfBoundsException {
        if (index < 0 || index > p.getBody().size()) {
            throw new IndexOutOfBoundsException();
        }
        this.p = p;
        this.index = index;
    } 
    
    public Production getProduction() {
        return p;
    }
    
    public int getIndex() {
        return index;
    }
    
    public NonTerminal getHead() {
        return p.getHead();
    }
    
    public Symbol<?> getNextSymbol() {
        if (isAtEnd()) {
            return null;
        } else {
            return p.getBodySymbol(index);
        }
    } 
    
    public boolean isAtEnd() {
        return index == p.getBody().size();
    }  
    
    @Override
    public String toString() {
        int size = p.getBody().size();
        StringBuilder s = new StringBuilder(p.getHead() + " -> [");
        for (int i = 0; i < index-1; i++) {
            s.append(p.getBodySymbol(i)).append(",");
        }
        if (index > 0) {
            s.append(p.getBodySymbol(index - 1));
        }
        s.append(".");
        for (int i = index; i < size-1; i++) {
            s.append(p.getBodySymbol(i)).append(",");
        }
        if (size > 0 && index <= size-1) {
            s.append(p.getBodySymbol(size - 1));
        }
        s.append("]");
        return s.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Item)) {
            return false;
        }
        Item i = (Item) o;
        return p.equals(i.p) && index == i.index;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(p, index);
    }
}
