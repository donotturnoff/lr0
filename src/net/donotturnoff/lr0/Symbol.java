package net.donotturnoff.lr0;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;
import java.util.Map;

public abstract class Symbol<T> implements Serializable {
    protected Node node;
    protected String name;
    protected transient Map<String, String> metadata = new HashMap<>();
    protected transient Map<String, Range> ranges = new HashMap<>();

    public void setNode(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public String getName() {
        return name;
    }
    
    public String getMetadata(String key) {
        return metadata.getOrDefault(key, null);
    }

    public void putMetadata(String key, String value) {
        metadata.put(key, value);
    }

    public Range getRange(String key) {
        return ranges.getOrDefault(key, null);
    }

    public Map<String, Range> getRanges() {
        return ranges;
    }

    public void putRange(String key, int start, int end) {
        ranges.put(key, new Range(start, end));
    }

    public void mergeRanges(Map<String, Range> ranges) {
        for (String key: ranges.keySet()) {
            this.ranges.put(key, new Range(this.ranges.get(key), ranges.get(key)));
        }
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
        if (!(o instanceof Symbol<?> s)) {
            return false;
        }
        return name.equals(s.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
