package net.donotturnoff.lr0;

import java.util.Map;
import java.util.HashMap;

public class Terminal<T> extends Symbol<T> {

    private final T value;
    
    protected Terminal() {
        this.name = null;
        this.value = null;
    }
    
    public Terminal(String name) {
        this.name = name.toUpperCase();
        this.value = null;
    }

    public Terminal(String name, Map<String, Range> ranges) {
        this.name = name.toUpperCase();
        this.ranges = ranges;
        this.value = null;
    }
    
    public Terminal(String name, Map<String, Range> ranges, T value) {
        this.name = name.toUpperCase();
        this.ranges = ranges;
        this.value = value;
    }

    public Terminal(String name, Map<String, Range> ranges, T value, Map<String, String> metadata) {
        this.name = name.toUpperCase();
        this.ranges = ranges;
        this.value = value;
        this.metadata = metadata;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        boolean hasValue = value != null;
        if (hasValue) {
            return name + "[" + value + "]";
        } else {
            return name;
        }
    }
}
