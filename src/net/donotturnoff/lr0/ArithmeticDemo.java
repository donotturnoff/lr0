package net.donotturnoff.lr0;

import java.util.Queue;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

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

        Set<Symbol<?>> symbols = new HashSet<>();

        symbols.add(tLparen);
        symbols.add(tRparen);
        symbols.add(tPlus);
        symbols.add(tMinus);
        symbols.add(tTimes);
        symbols.add(tCosine);
        symbols.add(tFactorial);
        symbols.add(tUFloat);

        symbols.add(ntStart);
        symbols.add(ntPlus);
        symbols.add(ntMinus);
        symbols.add(ntTimes);
        symbols.add(ntCosine);
        symbols.add(ntFactorial);
        symbols.add(ntFactor);
        symbols.add(ntFloat);

        Set<Production> productions = new HashSet<>();
        productions.add(new Production(ntStart, List.of(ntPlus)));
        productions.add(new Production(ntPlus, List.of(ntPlus, tPlus, ntMinus)));
        productions.add(new Production(ntPlus, List.of(ntMinus)));
        productions.add(new Production(ntMinus, List.of(ntMinus, tMinus, ntTimes)));
        productions.add(new Production(ntMinus, List.of(ntTimes)));
        productions.add(new Production(ntTimes, List.of(ntCosine, tTimes, ntTimes)));
        productions.add(new Production(ntTimes, List.of(ntCosine)));
        productions.add(new Production(ntCosine, List.of(tCosine, ntFactorial)));
        productions.add(new Production(ntCosine, List.of(ntFactorial)));
        productions.add(new Production(ntFactorial, List.of(ntFactor, tFactorial)));
        productions.add(new Production(ntFactorial, List.of(ntFactor)));
        productions.add(new Production(ntFactor, List.of(tLparen, ntPlus, tRparen)));
        productions.add(new Production(ntFactor, List.of(ntFloat)));
        productions.add(new Production(ntFloat, List.of(tPlus, tUFloat)));
        productions.add(new Production(ntFloat, List.of(tMinus, tUFloat)));
        productions.add(new Production(ntFloat, List.of(tUFloat)));

        Grammar g = new Grammar(symbols, productions, ntStart);

        try {
            Parser p = new Parser(g);
            System.out.println(p.parse(tokens));
        } catch (ParsingException e) {
            System.out.println("ParsingException: " + e.getMessage());
        }
        System.out.println();
    }
}
