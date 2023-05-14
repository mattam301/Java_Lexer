package LL1Parser.utils;

import LL1Parser.ParseTable;

import java.util.*;

public class FirstFollowSetsGenerator {

    private static final String EPSILON = "epsilon";
    private Map<String, List<List<String>>> grammar;
    private static Map<String, Set<String>> firstSets;
    private static Map<String, Set<String>> followSets;

    public FirstFollowSetsGenerator(Map<String, List<List<String>>> grammar) {
        this.grammar = grammar;
        firstSets = new HashMap<>();
        followSets = new HashMap<>();
        generateFirstSets();
        generateFollowSets();
    }

    private static boolean isTerminal(List<String> terminals ,String symbol) {
        return terminals.contains(symbol) || symbol.equals(EPSILON);
    }

    public static Map<String, Set<String>> calculateFirstSets(Map<String, List<List<String>>> grammar, List<String> terminals) {
        firstSets = new HashMap<>();

        // find FIRST set for all non-terminals
        for (String nonTerminal : grammar.keySet()) {
            calculateFirstSet(grammar, firstSets, nonTerminal, terminals);
        }

        return firstSets;
    }

    private static void calculateFirstSet(Map<String, List<List<String>>> grammar, Map<String, Set<String>> firstSets,
                                          String nonTerminal, List<String> terminals) {
        // if FIRST of current non-terminal is calculated, return
        if (firstSets.containsKey(nonTerminal)) {
            return;
        }

        Set<String> firstSet = new HashSet<>();
        // Search for the first character in each production
        for (List<String> production : grammar.get(nonTerminal)) {
            // get first character
            String symbol = production.get(0);

            // if the character is terminal, add it in
            if (isTerminal(terminals, symbol)) {
                firstSet.add(symbol);
            } else { // if not, find its FIRST
                calculateFirstSet(grammar, firstSets, symbol, terminals);
                // get the FIRST set
                Set<String> firstNext = new HashSet<>(firstSets.get(symbol));
                // if the set doesn't contain EPS, add all elements in
                if (!firstNext.contains(EPSILON)) {
                    firstSet.addAll(firstNext);
                }
                else { // if contains EPS, add all the FIRST of next symbols except EPS
                    int current = 0;
                    while (firstNext.contains(EPSILON)) {
                        firstNext.remove(EPSILON);
                        firstSet.addAll(firstNext);

                        symbol = (current + 1 < production.size()) ? production.get(current + 1) : null;
                        current++;

                        // if they all contain EPS, add EPS to FIRST
                        if (symbol == null) {
                            firstSet.add(EPSILON);
                            break;
                        }

                        firstNext = new HashSet<>(firstSets.get(symbol));
                    }
                }
            }
        }

        firstSets.put(nonTerminal, firstSet);
    }

    public static Map<String, Set<String>> calculateFollowSets(Map<String, List<List<String>>> grammar, List<String> terminals,
                                                               String startSymbol) {
        followSets = new HashMap<>();
        // calculate FIRST set if it hasn't been
        if (firstSets.isEmpty()) {
            firstSets = calculateFirstSets(grammar, terminals);
        }

        // calculate FOLLOW for all non-terminals
        for (String nonTerminal : grammar.keySet()) {
            calculateFollowSet(grammar, followSets, firstSets, nonTerminal, terminals, startSymbol);
        }

        return followSets;
    }

    private static void calculateFollowSet(Map<String, List<List<String>>> grammar, Map<String, Set<String>> followSets,
                                           Map<String, Set<String>> firstSets, String nonTerminal, List<String> terminals, String startSymbol) {
        // if FOLLOW of current non-terminal is calculated, return
        if (followSets.containsKey(nonTerminal)) {
            return;
        }

        Set<String> followSet = new HashSet<>();

        // add '$' symbol for starting state
        if (nonTerminal.equals(startSymbol)) {
            followSet.add("$");
        }
//        System.out.println(nonTerminal);
        // iterate all elements in grammar
        for (Map.Entry<String, List<List<String>>> entry : grammar.entrySet()) {
            String otherNonTerminal = entry.getKey();
            List<List<String>> productions = entry.getValue();

            // iterate all production of the current non-terminal
            for (List<String> production : productions) {
                for (int i = 0; i < production.size(); i++) {
                    String symbol = production.get(i);

                    // find the symbol in the right side
                    if (symbol.equals(nonTerminal)) {
                        String nextSymbol = (i + 1 < production.size()) ? production.get(i + 1) : null;

                        // if the symbol is the right-most symbol, add FOLLOW of the left-side symbol
                        if (nextSymbol == null) {
                            if (followSets.containsKey(otherNonTerminal)) {
                                followSet.addAll(followSets.get(otherNonTerminal));
                            }
                        } else { // if not
                            // if next symbol is terminal, add in follow set
                            if (isTerminal(terminals, nextSymbol)) {
                                followSet.add(nextSymbol);
                            } else {
                                // if next symbol is non-terminal, iterate all next symbols
                                int current = i + 1;
                                Set<String> firstNext = new HashSet<>(firstSets.get(nextSymbol));

                                if (!firstNext.contains(EPSILON)) {
                                    followSet.addAll(firstNext);
                                } else {
                                    while (firstNext.contains(EPSILON)) {
                                        // since FOLLOW set should not include EPS, remove it before add
                                        firstNext.remove(EPSILON);
                                        followSet.addAll(firstNext);

                                        nextSymbol = (current + 1 < production.size()) ? production.get(current + 1) : null;
                                        current++;

                                        // if all next symbol contain EPS, current symbol is the right-most symbol
                                        // -> add FOLLOW set of the left side symbol in
                                        if (nextSymbol == null) {
                                            followSet.addAll(followSets.get(otherNonTerminal));
                                            break;
                                        } else if (isTerminal(terminals, nextSymbol)) {
                                            // if next symbol is terminal, add in FOLLOW set and stop the while loop
                                            followSet.add(nextSymbol);
                                            break;
                                        }
                                        firstNext = new HashSet<>(firstSets.get(nextSymbol));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        followSets.put(nonTerminal, followSet);
    }

    private void generateFirstSets() {
        for (String nonTerminal : grammar.keySet()) {
            firstSets.put(nonTerminal, new HashSet<>());
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (String nonTerminal : grammar.keySet()) {
                int oldSize = firstSets.get(nonTerminal).size();
                for (List<String> production : grammar.get(nonTerminal)) {
                    if (production.isEmpty()) {
                        firstSets.get(nonTerminal).add("epsilon");
                    } else {
                        String firstSymbol = production.get(0);
                        if (!grammar.containsKey(firstSymbol)) {
                            firstSets.get(nonTerminal).add(firstSymbol);
                        } else {
                            firstSets.get(nonTerminal).addAll(firstSets.get(firstSymbol));
                            if (firstSets.get(firstSymbol).contains("epsilon")) {
                                firstSets.get(nonTerminal).add("epsilon");
                            }
                        }
                    }
                }
                int newSize = firstSets.get(nonTerminal).size();
                if (newSize > oldSize) {
                    changed = true;
                }
            }
        }
    }

    private void generateFollowSets() {
        for (String nonTerminal : grammar.keySet()) {
            followSets.put(nonTerminal, new HashSet<>());
        }
        followSets.get(getStartSymbol()).add("$");
        boolean changed = true;
        while (changed) {
            changed = false;
            for (String nonTerminal : grammar.keySet()) {
                int oldSize = followSets.get(nonTerminal).size();
                for (List<String> production : grammar.get(nonTerminal)) {
                    for (int i = 0; i < production.size(); i++) {
                        String symbol = production.get(i);
                        if (grammar.containsKey(symbol)) {
                            if (i < production.size() - 1) {
                                String nextSymbol = production.get(i + 1);
                                if (!grammar.containsKey(nextSymbol)) {
                                    followSets.get(symbol).add(nextSymbol);
                                } else {
                                    followSets.get(symbol).addAll(firstSets.get(nextSymbol));
                                    if (firstSets.get(nextSymbol).contains("epsilon")) {
                                        followSets.get(symbol).addAll(followSets.get(nonTerminal));
                                    }
                                }
                            } else {
                                followSets.get(symbol).addAll(followSets.get(nonTerminal));
                            }
                        }
                    }
                }
                int newSize = followSets.get(nonTerminal).size();
                if (newSize > oldSize) {
                    changed = true;
                }
            }
        }
    }

    public Map<String, Set<String>> getFirstSets() {
        return firstSets;
    }

    public Map<String, Set<String>> getFollowSets() {
        return followSets;
    }

    public String getStartSymbol() {
        return grammar.keySet().iterator().next();
    }

    public static void main(String[] args) {

    }
}
