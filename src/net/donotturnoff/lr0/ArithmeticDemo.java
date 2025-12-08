package net.donotturnoff.lr0;

import java.util.Queue;
import java.util.List;

public class ArithmeticDemo {
    public static void main(String[] args) {
        String s = String.join(" ", args);
        parse(s);
    }

    private static void parse(String s) {
        System.out.println("Parsing " + s);
        Lexer l = new Lexer();
        Queue<Terminal<?>> tokens;
        try {
            tokens = l.lex(s);
        } catch (LexingException e) {
            System.out.println("LexingException: " + e.getMessage());
            return;
        }

        NonTerminal ntStart = new NonTerminal("start");
        NonTerminal ntPlus = new NonTerminal("plus");
        NonTerminal ntMinus = new NonTerminal("minus");
        NonTerminal ntTimes = new NonTerminal("times");
        NonTerminal ntCosine = new NonTerminal("cosine");
        NonTerminal ntFactorial = new NonTerminal("factorial");
        NonTerminal ntFactor = new NonTerminal("factor");
        NonTerminal ntFloat = new NonTerminal("float");

        Terminal<Void> tLparen = new Terminal<>("LPAREN");
        Terminal<Void> tRparen = new Terminal<>("RPAREN");
        Terminal<Void> tPlus = new Terminal<>("PLUS");
        Terminal<Void> tMinus = new Terminal<>("MINUS");
        Terminal<Void> tTimes = new Terminal<>("TIMES");
        Terminal<Void> tCosine = new Terminal<>("COSINE");
        Terminal<Void> tFactorial = new Terminal<>("FACTORIAL");
        Terminal<Void> tUFloat = new Terminal<>("U_FLOAT");

        Productions productions = new Productions();
        productions.add(ntStart, List.of(ntPlus));
        productions.add(ntPlus, List.of(ntPlus, tPlus, ntMinus));
        productions.add(ntPlus, List.of(ntMinus));
        productions.add(ntMinus, List.of(ntMinus, tMinus, ntTimes));
        productions.add(ntMinus, List.of(ntTimes));
        productions.add(ntTimes, List.of(ntCosine, tTimes, ntTimes));
        productions.add(ntTimes, List.of(ntCosine));
        productions.add(ntCosine, List.of(tCosine, ntFactorial));
        productions.add(ntCosine, List.of(ntFactorial));
        productions.add(ntFactorial, List.of(ntFactor, tFactorial));
        productions.add(ntFactorial, List.of(ntFactor));
        productions.add(ntFactor, List.of(tLparen, ntPlus, tRparen));
        productions.add(ntFactor, List.of(ntFloat));
        productions.add(ntFloat, List.of(tPlus, tUFloat));
        productions.add(ntFloat, List.of(tMinus, tUFloat));
        productions.add(ntFloat, List.of(tUFloat));

        try {
            Grammar g = new Grammar(productions, ntStart);
            Parser p = new Parser(g);
            System.out.println(p.parse(tokens));
        } catch (GrammarException e) {
            System.out.println("GrammarException: " + e.getMessage());
        } catch (ParsingException e) {
            System.out.println("ParsingException: " + e.getMessage());
        }
        System.out.println();
    }
}
