package LL1Parser;

import java.util.*;

public class FirstFollowSets {
    private static final String EPSILON = "epsilon";

    private static boolean isTerminal(String symbol) {
        return !Character.isUpperCase(symbol.charAt(0));
    }

    public static Map<String, Set<String>> calculateFirstSets(Map<String, List<List<String>>> grammar) {
        Map<String, Set<String>> firstSets = new HashMap<>();

        for (String nonTerminal : grammar.keySet()) {
            calculateFirstSet(grammar, firstSets, nonTerminal);
        }

        return firstSets;
    }

    private static void calculateFirstSet(Map<String, List<List<String>>> grammar, Map<String, Set<String>> firstSets,
                                          String nonTerminal) {
        if (firstSets.containsKey(nonTerminal)) {
            return;
        }

        Set<String> firstSet = new HashSet<>();

        for (List<String> production : grammar.get(nonTerminal)) {
            String symbol = production.get(0);

            if (isTerminal(symbol)) {
                firstSet.add(symbol);
            } else {
                calculateFirstSet(grammar, firstSets, symbol);
                Set<String> firstNext = new HashSet<>(firstSets.get(symbol));
                if (!firstNext.contains(EPSILON)) {
                    firstSet.addAll(firstNext);
                }
                else {
                    int current = 0;
                    while (firstNext.contains(EPSILON)) {
                        firstNext.remove(EPSILON);
                        firstSet.addAll(firstSets.get(firstNext));

                        symbol = (current + 1 < production.size()) ? production.get(current + 1) : null;
                        current++;

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

    public static Map<String, Set<String>> calculateFollowSets(Map<String, List<List<String>>> grammar,
                                                               String startSymbol) {
        Map<String, Set<String>> followSets = new HashMap<>();
        Map<String, Set<String>> firstSets = calculateFirstSets(grammar);

        for (String nonTerminal : grammar.keySet()) {
            calculateFollowSet(grammar, followSets, firstSets, nonTerminal, startSymbol);
        }

        return followSets;
    }

    private static void calculateFollowSet(Map<String, List<List<String>>> grammar, Map<String, Set<String>> followSets,
                                           Map<String, Set<String>> firstSets, String nonTerminal, String startSymbol) {
        if (followSets.containsKey(nonTerminal)) {
            return;
        }

        Set<String> followSet = new HashSet<>();

        if (nonTerminal.equals(startSymbol)) {
            followSet.add("$");
        }

        for (Map.Entry<String, List<List<String>>> entry : grammar.entrySet()) {
            String otherNonTerminal = entry.getKey();
            List<List<String>> productions = entry.getValue();

            for (List<String> production : productions) {
                for (int i = 0; i < production.size(); i++) {
                    String symbol = production.get(i);

                    if (symbol.equals(nonTerminal)) {
                        String nextSymbol = (i + 1 < production.size()) ? production.get(i + 1) : null;

                        if (nextSymbol == null) {
                            if (followSets.containsKey(otherNonTerminal)) {
                                followSet.addAll(followSets.get(otherNonTerminal));
                            }
                        } else {
                            if (isTerminal(nextSymbol)) {
                                followSet.add(nextSymbol);
                            } else {
                                int current = i + 1;
                                Set<String> firstNext = new HashSet<>(firstSets.get(nextSymbol));
                                while (firstNext.contains(EPSILON)) {
                                    firstNext.remove(EPSILON);
                                    followSet.addAll(firstNext);

                                    nextSymbol = (current + 1 < production.size()) ? production.get(current + 1) : null;
                                    current++;

                                    if (nextSymbol == null) {
                                        followSet.addAll(followSets.get(otherNonTerminal));
                                        break;
                                    } else if (isTerminal(nextSymbol)) {
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

        followSets.put(nonTerminal, followSet);
    }

    public static void main(String[] args) {
        // Example grammar
        Map<String, List<List<String>>> grammar = new LinkedHashMap<>();
        grammar.put("E", List.of(Arrays.asList("T", "E'")));
        grammar.put("E'", Arrays.asList(Arrays.asList("+", "T", "E'"),
                Arrays.asList("-", "T", "E'"),
                Collections.singletonList(EPSILON)));
        grammar.put("T", List.of(Arrays.asList("F", "T'")));
        grammar.put("T'", Arrays.asList(Arrays.asList("*", "F", "T'"),
                Arrays.asList("/", "F", "T'"),
                Collections.singletonList(EPSILON)));
        grammar.put("F", Arrays.asList(Arrays.asList("(", "E", ")"),
                Collections.singletonList("id"),
                Collections.singletonList("num")));

        String startSymbol = "E";

        // Calculate FIRST sets
        Map<String, Set<String>> firstSets = calculateFirstSets(grammar);

        // Calculate FOLLOW sets
        Map<String, Set<String>> followSets = calculateFollowSets(grammar, startSymbol);

        // Print the FIRST sets
        System.out.println("FIRST Sets:");
        for (Map.Entry<String, Set<String>> entry : firstSets.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        // Print the FOLLOW sets
        System.out.println("\nFOLLOW Sets:");
        for (Map.Entry<String, Set<String>> entry : followSets.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}