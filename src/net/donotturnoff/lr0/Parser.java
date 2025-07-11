package net.donotturnoff.lr0;

import java.io.*;
import java.util.*;

public class Parser implements Serializable {
    
    private Table<Set<Item>, Terminal<?>, Action> action;
    private Table<Set<Item>, NonTerminal, Set<Item>> goTo;
    private Set<Item> start;
    
    private transient Queue<Terminal<?>> tokens;
    private transient Terminal<?> lastToken;
    private transient int i = -1;
    
    public Parser(Grammar g) {
        generateParser(g);
    }

    public Parser(String path) throws IOException, ClassNotFoundException {
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Parser p = (Parser) ois.readObject();
            ois.close();
            this.action = p.action;
            this.goTo = p.goTo;
            this.start = p.start;
    }

    public void save(String path) throws IOException {
        FileOutputStream fos = new FileOutputStream(path);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.close();
    }

    private void generateParser(Grammar g) {
        TableGenerator tg = new TableGenerator(g);
        action = tg.getAction();
        goTo = tg.getGoTo();
        start = tg.getStart();
        lastToken = new EOF();
    }
    
    private Terminal<?> getToken() {
        Terminal<?> nextToken = tokens.poll();
        if (nextToken == null) {
            return new EOF();
        }
        i++;
        return nextToken;
    }
    
    public Node parse(Queue<Terminal<?>> tokens) throws ParsingException {
        if (tokens.isEmpty()) {
            return null;
        }
        
        Stack<Frame> stack = new Stack<>();
        stack.push(new Frame(start, new Node(new AugmentedStartSymbol())));
    
        this.tokens = tokens;
        Terminal<?> nextToken = getToken();
        
        while (true) {
            Frame f = stack.peek();
            Action a = action.get(f.getState(), nextToken);
            if (a == null) {
                throw new ParsingException("Unexpected token: " + lastToken + " at " + lastToken.getRanges());
            } else if (a instanceof ShiftAction s) {
                nextToken.putRange("tokens", i, i);
                stack.push(new Frame(s.getNextState(), new Node(nextToken)));
                lastToken = nextToken;
                nextToken = getToken();
            } else if (a instanceof ReduceAction r) {
                Production p = r.getProduction();

                List<Node> children = new LinkedList<>();
                for (int i = 0; i < p.getBody().size(); i++) {
                    Frame top = stack.pop();
                    children.add(0, top.getNode());
                }

                Frame top = stack.peek();
                NonTerminal head = new NonTerminal(p.getHead().getName());
                Set<Item> nextState = goTo.get(top.getState(), head);
                head.mergeRanges(children.get(0).getSymbol().getRanges());
                head.mergeRanges(children.get(children.size()-1).getSymbol().getRanges());
                stack.push(new Frame(nextState, new Node(head, children)));
            } else if (a instanceof AcceptAction) {
                break;
            }
        }
        return stack.pop().getNode();
    }
}
