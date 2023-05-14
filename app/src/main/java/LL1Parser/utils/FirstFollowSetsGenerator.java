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
        for (String nonTerminal : grammar.keySet()) {
            followSets.put(nonTerminal, new HashSet<>());
        }
        String startSymbol = grammar.keySet().iterator().next();
        followSets.get(startSymbol).add("$");
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Map.Entry<String, List<List<String>>> entry : grammar.entrySet()) {
                String nonTerminal = entry.getKey();
                List<List<String>> rhsList = entry.getValue();
                for (List<String> rhs : rhsList) {
                    for (int i = 0; i < rhs.size(); i++) {
                        String symbol = rhs.get(i);
                        if (grammar.containsKey(symbol)) {
                            Set<String> followSet = followSets.get(symbol);
                            int oldSize = followSet.size();
                            if (i == rhs.size() - 1) {
                                followSet.addAll(followSets.get(nonTerminal));
                            } else {
                                String nextSymbol = rhs.get(i + 1);
                                Set<String> firstSetOfNextSymbol = firstSets.get(nextSymbol);
                                if (firstSetOfNextSymbol != null) {
                                    for (String terminal : firstSetOfNextSymbol) {
                                        if (!terminal.equals("EPSILON")) {
                                            followSet.add(terminal);
                                        }
                                    }
                                    int j = i + 1;
                                    while (j < rhs.size() && firstSetOfNextSymbol.contains("EPSILON")) {
                                        j++;
                                        if (j < rhs.size()) {
                                            nextSymbol = rhs.get(j);
                                            firstSetOfNextSymbol = firstSets.get(nextSymbol);
                                            if (firstSetOfNextSymbol != null) {
                                                for (String terminal : firstSetOfNextSymbol) {
                                                    if (!terminal.equals("EPSILON")) {
                                                        followSet.add(terminal);
                                                    }
                                                }
                                            } else {
                                                followSet.add(nextSymbol);
                                                break;
                                            }
                                        } else if (j == rhs.size()) {
                                            followSet.addAll(followSets.get(nonTerminal));
                                        }
                                    }
                                } else {
                                    followSet.add(nextSymbol);
                                }
                            }
                            int newSize = followSet.size();
                            if (newSize > oldSize) {
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
