package net.donotturnoff.lr0;

import java.util.Objects;

public abstract class Symbol<T> {
    protected String name;

    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Symbol)) {
            return false;
        }
        Symbol<?> s = (Symbol<?>) o;
        return name.equals(s.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
