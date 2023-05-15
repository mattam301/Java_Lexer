package LL1Parser.utils;

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
                        firstSets.get(nonTerminal).add("EPSILON");
                    } else {
                        String firstSymbol = production.get(0);
                        if (!grammar.containsKey(firstSymbol)) {
                            firstSets.get(nonTerminal).add(firstSymbol);
                        } else {
                            firstSets.get(nonTerminal).addAll(firstSets.get(firstSymbol));
                            if (firstSets.get(firstSymbol).contains("EPSILON")) {
                                firstSets.get(nonTerminal).add("EPSILON");
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
        followSets.put("<program>", new HashSet<>(List.of("$")));
        boolean changed = true;
        while (changed) {
            changed = false;
            for (String nonTerminal : grammar.keySet()) {
                if (!followSets.containsKey(nonTerminal)) {
                    followSets.put(nonTerminal, new HashSet<>());
                }
                for (List<String> production : grammar.get(nonTerminal)) {
                    for (int i = 0; i < production.size(); i++) {
                        String symbol = production.get(i);
                        if (grammar.containsKey(symbol)) {
                            if (!followSets.containsKey(symbol)) {
                                followSets.put(symbol, new HashSet<>());
                            }
                            int oldSize = followSets.get(symbol).size();
                            if (i == production.size() - 1) {
                                followSets.get(symbol).addAll(followSets.get(nonTerminal));
                            } else {
                                String nextSymbol = production.get(i + 1);
                                if (firstSets.containsKey(nextSymbol)) {
                                    Set<String> tempFirstSet = new HashSet<>(firstSets.get(nextSymbol));
                                    tempFirstSet.remove("epsilon");
                                    followSets.get(symbol).addAll(tempFirstSet);
                                    if (firstSets.get(nextSymbol).contains("epsilon")) {
                                        followSets.get(symbol).addAll(followSets.get(nonTerminal));
                                    }
                                } else {
                                    followSets.get(symbol).add(nextSymbol);
                                }
                            }
                            if (followSets.get(symbol).size() > oldSize) {
                                changed = true;
                            }
                        }
                    }
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

}
