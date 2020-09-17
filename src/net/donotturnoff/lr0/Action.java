package net.donotturnoff.lr0;

public class Action<T> {
    public static final int SHIFT = 0, REDUCE = 1, ACCEPT = 2;
    private static final String[] NAMES = new String[]{"Shift", "Reduce", "Accept"};
    
    private final int type;
    private final T data;
    
    public Action(int type) {
        this.type = type;
        this.data = null;
    }
    
    public Action(int type, T data) {
        this.type = type;
        this.data = data;
    }
    
    public int getType() {
        return type;
    }
    
    public T getData() {
        return data;
    }
    
    @Override
    public String toString() {
        return NAMES[type] + "(" + data + ")";
    }
}
