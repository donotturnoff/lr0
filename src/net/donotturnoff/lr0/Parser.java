package net.donotturnoff.lr0;

import java.util.Queue;
import java.util.Stack;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;

public class Parser {
    
    private final Table<Set<Item>, Terminal<?>, Action<?>> action;
    private final Table<Set<Item>, NonTerminal, Set<Item>> goTo;
    private final Set<Item> start;
    
    private Terminal<?> nextToken;
    private Queue<Terminal<?>> tokens;
    private Terminal<?> lastTerminal;
    
    public Parser(Grammar g) {
        TableGenerator tg = new TableGenerator(g);
        action = tg.getAction();
        goTo = tg.getGoTo();
        start = tg.getStart();
        lastTerminal = new EOF();
    }
    
    private void getToken() {
        nextToken = tokens.poll();
    }
    
    public Node parse(Queue<Terminal<?>> tokens) throws ParsingException {

        if (tokens.isEmpty()) {
            return null;
        }
        
        Stack<Frame> stack = new Stack<>();
        stack.push(new Frame(start, new Node(new AugmentedStartSymbol())));
    
        this.tokens = tokens;
        getToken();
        
        while (true) {
            Frame f = stack.peek();
            Action<?> a = action.get(f.getState(), nextToken);
            if (a == null) {
                Token<?> t = lastTerminal.getToken();
                if (t != null) {
                    int line = t.getLine();
                    int column = t.getColumn();
                    throw new ParsingException("Unexpected token: " + lastTerminal + " (line " + line + ", column " + column + ")");
                } else {
                    throw new ParsingException("Unexpected token: " + lastTerminal);
                }
            } else if (a.getType() == Action.SHIFT) {
                stack.push(new Frame((Set<Item>) a.getData(), new Node(nextToken)));
                lastTerminal = nextToken;
                getToken();
            } else if (a.getType() == Action.REDUCE) {
                Production p = (Production) a.getData();
                List<Node> children = new LinkedList<>();
                for (int i = 0; i < p.getBody().size(); i++) {
                    Frame top = stack.pop();
                    children.add(0, top.getNode());
                }
                Frame top = stack.peek();
                Set<Item> nextState = goTo.get(top.getState(), p.getHead());
                stack.push(new Frame(nextState, new Node(p.getHead(), children)));
            } else if (a.getType() == Action.ACCEPT) {
                break;
            }
        }
        return stack.pop().getNode();
    }
}
