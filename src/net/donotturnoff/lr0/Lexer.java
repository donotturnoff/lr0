package net.donotturnoff.lr0;

import java.util.*;

public class Lexer {

    private String text;
    private int i, line, column;
    private boolean end;
    private char nextChar;
    
    public Lexer() {
        this.i = 0;
        this.line = 0;
        this.column = 0;
        this.end = false;
    }

    private void getChar() {
        if (i == text.length()) {
            end = true;
        } else {
            nextChar = text.charAt(i);
            i++;
            column++;
            end = false;
        }
        if (nextChar == '\n') {
            line++;
            column = 0;
        }
    }

    public Queue<Terminal<?>> lex(String text) throws LexingException {
        this.text = text;
        getChar();
        
        Queue<Terminal<?>> tokens = new LinkedList<>();
        Terminal<?> t;
        do {
            t = nextToken();
            tokens.add(t);
        } while (! (t instanceof EOF));
        return tokens;
    }
    
    private Map<String, Range> positionMetadata() {
        return new HashMap<>(Map.of(
            "line", new Range(line, line), // TODO: show entire range
            "column", new Range(column, column),
            "index", new Range(i, i)
        ));
    }

    private Terminal<Void> consumeCos() throws LexingException {
        getChar();
        if (end) {
            throw new LexingException("Expected \"cos\", got \"c\" (line " + line + ", column " + column + ")");
        }
        
        char snd = nextChar;
        getChar();
        if (end) {
            throw new LexingException("Expected \"cos\", got \"c" + snd + "\" (line " + line + ", column " + column + ")");
        }
        
        char thd = nextChar;
        getChar();
        if (snd == 'o' && thd == 's') {
            return new Terminal<>("COSINE", positionMetadata());
        } else {
            throw new LexingException("Expected \"cos\", got \"c" + snd + thd + "\" (line " + line + ", column " + column + ")");
        }
    }

    private Terminal<Double> consumeUnsignedFloat() throws LexingException {
        StringBuilder s = new StringBuilder();
        while (Character.isDigit(nextChar) && !end) {
            s.append(nextChar);
            getChar();
        }
        if (nextChar == '.') {
            s.append(nextChar);
            getChar();
            if (end) {
                throw new LexingException("Expected digit following decimal point, got EOF (line " + line + ", column " + column + ")");
            } else if (!Character.isDigit(nextChar)) {
                throw new LexingException("Expected digit following decimal point, got \"" + nextChar + "\" (line " + line + ", column " + column + ")");
            }
            do {
                s.append(nextChar);
                getChar();
            } while (Character.isDigit(nextChar) && !end);
        }
        return new Terminal<>("U_FLOAT", positionMetadata(), Double.parseDouble(s.toString()));
    }

    private void consumeWhiteSpace() {
        while (!end && Character.isWhitespace(nextChar)) {
            getChar();
        }
    }

    private Terminal<?> nextToken() throws LexingException {
        consumeWhiteSpace();
        Terminal<?> t;
        if (end) {
            t = new EOF();
        } else {
            switch (nextChar) {
                case '(':
                    t = new Terminal<Void>("LPAREN", positionMetadata()); getChar();
                    break;
                case ')':
                    t = new Terminal<Void>("RPAREN", positionMetadata()); getChar();
                    break;
                case '+':
                    t = new Terminal<Void>("PLUS", positionMetadata()); getChar();
                    break;
                case '-':
                    t = new Terminal<Void>("MINUS", positionMetadata()); getChar();
                    break;
                case '*':
                    t = new Terminal<Void>("TIMES", positionMetadata()); getChar();
                    break;
                case '!':
                    t = new Terminal<Void>("FACTORIAL", positionMetadata()); getChar();
                    break;
                case 'c':
                    t = consumeCos();
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    t = consumeUnsignedFloat();
                    break;
                default:
                    throw new LexingException("Unexpected token initial: \"" + nextChar + "\" (line " + line + ", column " + column + ")");
            }
        }
        return t;
    }
}
