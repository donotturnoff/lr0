package net.donotturnoff.lr0;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

public class Table<T, U, V> implements Serializable {
    private final Map<T, Map<U, V>> contents;
    
    public Table() {
        this.contents = new HashMap<>();
    }
    
    public void put(T t, U u, V value) {
        Map<U, V> m = contents.get(t);
        if (m == null) {
            contents.put(t, new HashMap<>());
            m = contents.get(t);
        }
        m.put(u, value);
    }
    
    public V get(T t, U u) {
        Map<U, V> m = contents.get(t);
        if (m == null) {
            return null;
        } else {
            return m.get(u);
        }
    }
    
    @Override
    public String toString() {
        return "net.donotturnoff.lr0.Table" + contents.toString();
    }
}
