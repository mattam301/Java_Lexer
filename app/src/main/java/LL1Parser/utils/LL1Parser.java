package LL1Parser.utils;

import java.util.*;

public class LL1Parser {
    private Map<String, Set<String>> firstSets;
    private Map<String, Set<String>> followSets;
    private Map<String, List<List<String>>> grammar;
    private Map<String, Map<String, List<String>>> parseTable;

    public LL1Parser(Map<String, Set<String>> firstSets,
                     Map<String, Set<String>> followSets,
                     Map<String, List<List<String>>> grammar) {
        this.firstSets = firstSets;
        this.followSets = followSets;
        this.grammar = grammar;
        this.parseTable = new HashMap<>();
        generateParseTable();
    }

    private void generateParseTable() {
        for (Map.Entry<String, List<List<String>>> entry : grammar.entrySet()) {
            String nonTerminal = entry.getKey();
            List<List<String>> productions = entry.getValue();
            for (List<String> production : productions) {
                Set<String> firstSet = getFirstSet(production);
                for (String terminal : firstSet) {
                    if (!terminal.equals("epsilon")) {
                        addProductionToParseTable(nonTerminal, terminal, production);
                    }
                }
                if (firstSet.contains("epsilon")) {
                    Set<String> followSet = followSets.get(nonTerminal);
                    for (String terminal : followSet) {
                        addProductionToParseTable(nonTerminal, terminal, production);
                    }
                }
            }
        }
    }

    private Set<String> getFirstSet(List<String> production) {
        Set<String> firstSet = new HashSet<>();
        int index = 0;
        boolean hasEpsilon = true;
        while (index < production.size() && hasEpsilon) {
            hasEpsilon = false;
            String symbol = production.get(index);
            if (isTerminal(symbol)) {
                firstSet.add(symbol);
            } else {
                Set<String> symbolFirstSet = firstSets.get(symbol);
                firstSet.addAll(symbolFirstSet);
                if (symbolFirstSet.contains("epsilon")) {
                    hasEpsilon = true;
                    firstSet.remove("epsilon");
                }
            }
            index++;
        }
        if (hasEpsilon) {
            firstSet.add("epsilon");
        }
        return firstSet;
    }

    private boolean isTerminal(String symbol) {
        return !firstSets.containsKey(symbol);
    }

    private void addProductionToParseTable(String nonTerminal,
                                           String terminal,
                                           List<String> production) {
        if (!parseTable.containsKey(nonTerminal)) {
            parseTable.put(nonTerminal, new HashMap<>());
        }
        Map<String, List<String>> row = parseTable.get(nonTerminal);
        row.put(terminal, production);
    }

    public Map<String, Map<String, List<String>>> getParseTable() {
        return parseTable;
    }
}
