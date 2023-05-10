package LL1Parser;

import java.util.*;

public class ParseTable {

    private Map<String, Set<String>> firstSets;
    private Map<String, Set<String>> followSets;
    private Map<String, List<String>> productions;
    private Map<String, Map<String, String>> parseTable;

    public ParseTable(Map<String, Set<String>> firstSets, Map<String, Set<String>> followSets,
                      Map<String, List<String>> productions) {
        this.firstSets = firstSets;
        this.followSets = followSets;
        this.productions = productions;
        this.parseTable = new HashMap<>();
    }

    public void generateParseTable() {
        for (String nonTerminal : firstSets.keySet()) {
            parseTable.put(nonTerminal, new HashMap<>());
            for (String production : productions.get(nonTerminal)) {
                String[] symbols = production.split(" ");
                String firstSymbol = symbols[0];
                if (firstSets.containsKey(firstSymbol)) {
                    for (String terminal : firstSets.get(firstSymbol)) {
                        if (!terminal.equals("epsilon")) {
                            parseTable.get(nonTerminal).put(terminal, nonTerminal + " -> " + production);
                        } else {
                            for (String follow : followSets.get(nonTerminal)) {
                                parseTable.get(nonTerminal).put(follow, nonTerminal + " -> epsilon");
                            }
                        }
                    }
                } else if (!firstSymbol.equals("epsilon")) {
                    parseTable.get(nonTerminal).put(firstSymbol, nonTerminal + " -> " + production);
                } else {
                    for (String follow : followSets.get(nonTerminal)) {
                        parseTable.get(nonTerminal).put(follow, nonTerminal + " -> epsilon");
                    }
                }
            }
        }
    }

    public void printParseTable() {
        for (String nonTerminal : parseTable.keySet()) {
            System.out.println(nonTerminal + ": " + parseTable.get(nonTerminal));
        }
    }

    public static void main(String[] args) {
        // Sample first and follow sets
        Map<String, Set<String>> firstSets = new HashMap<>();
        firstSets.put("E", Set.of("num", "(", "id"));
        firstSets.put("E'", Set.of("epsilon", "+", "-"));
        firstSets.put("T", Set.of("num", "(", "id"));
        firstSets.put("T'", Set.of("epsilon", "*", "/"));
        firstSets.put("F", Set.of("num", "(", "id"));

        Map<String, Set<String>> followSets = new HashMap<>();
        followSets.put("E", Set.of("$", ")"));
        followSets.put("E'", Set.of("$", ")"));
        followSets.put("T", Set.of("$", ")", "+", "-"));
        followSets.put("T'", Set.of("$", ")", "+", "-"));
        followSets.put("F", Set.of("$", ")", "*", "+", "-", "/"));

        // Sample production rules
        Map<String, List<String>> productions = new HashMap<>();
        productions.put("E", Arrays.asList("T E'"));
        productions.put("E'", Arrays.asList("+ T E'", "- T E'", "epsilon"));
        productions.put("T", Arrays.asList("F T'"));
        productions.put("T'", Arrays.asList("* F T'", "/ F T'", "epsilon"));
        productions.put("F", Arrays.asList("( E )", "id", "num"));

        ParseTable parseTable = new ParseTable(firstSets, followSets, productions);
        parseTable.generateParseTable();
        parseTable.printParseTable();
    }
}

