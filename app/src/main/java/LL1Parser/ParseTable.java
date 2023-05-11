package LL1Parser;

import java.util.*;

public class ParseTable {

    private Map<String, Set<String>> firstSets;
    private Map<String, Set<String>> followSets;
    private Map<String, List<List<String>>> grammar;
    private static Map<String, Map<String, List<String>>> parseTable;

    public ParseTable(Map<String, Set<String>> firstSets, Map<String, Set<String>> followSets,
                      Map<String, List<List<String>>> grammar) {
        this.firstSets = firstSets;
        this.followSets = followSets;
        this.grammar = grammar;
        this.parseTable = new HashMap<>();
    }

    public void generateParseTable() {
        for (String nonTerminal : grammar.keySet()) {
            parseTable.put(nonTerminal, new HashMap<>());
            for (List<String> production : grammar.get(nonTerminal)) {
                String firstSymbol = production.get(0);
                if (firstSets.containsKey(firstSymbol)) {
                    for (String terminal : firstSets.get(firstSymbol)) {
                        if (!terminal.equals("epsilon")) {
                            parseTable.get(nonTerminal).put(terminal, production);
                        } else {
                            for (String follow : followSets.get(nonTerminal)) {
                                parseTable.get(nonTerminal).put(follow, Arrays.asList("epsilon"));
                            }
                        }
                    }
                } else if (!firstSymbol.equals("epsilon")) {
                    parseTable.get(nonTerminal).put(firstSymbol, production);
                } else {
                    for (String follow : followSets.get(nonTerminal)) {
                        parseTable.get(nonTerminal).put(follow, Arrays.asList("epsilon"));
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

    public static boolean parse(String input, Map<String, Map<String, List<String>>> parseTable, String startSymbol) {
        Stack<String> stack = new Stack<>();
        stack.push("$");
        stack.push(startSymbol);

        int i = 0;
        while (!stack.isEmpty()) {
            String top = stack.peek();
            if (top.equals("$") && i == input.length()) {
                return true;
            } else if (top.equals(input.charAt(i) + "")) {
                stack.pop();
                i++;
            } else if (parseTable.containsKey(top) && parseTable.get(top).containsKey(input.charAt(i) + "")) {
                stack.pop();
                List<String> production = parseTable.get(top).get(input.charAt(i) + "");
                for (int j = production.size() - 1; j >= 0; j--) {
                    if (!production.get(j).equals("epsilon")) {
                        stack.push(production.get(j));
                    }
                }
            } else {
                return false;
            }
        }

        return false;
    }

    public static boolean parse2(String input, Map<String, Map<String, List<String>>> parseTable, String startSymbol) {
        input += '$';
        Stack<String> stack = new Stack<>();
        stack.push("$");
        stack.push(startSymbol);

        int i = 0;
        while (!stack.isEmpty()) {
            String top = stack.peek();
            if (top.equals("$") && i == input.length()) {
                return true;
            }

            String current = input.charAt(i) + "";
            if (current.equals('1' + "")) {
                current = "num";
            }
            while (!top.equals(current)) {
                if (!parseTable.get(top).containsKey(current)) {
                    return false;
                }

                stack.pop();
                List<String> production = parseTable.get(top).get(current);
                for (int j = production.size() - 1; j >= 0; j--) {
                    if (!production.get(j).equals("epsilon")) {
                        stack.push(production.get(j));
                    }
                }
                top = stack.peek();
            }
            stack.pop();
            i++;
        }
        return true;
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
        Map<String, List<List<String>>> productions = new HashMap<>();
        productions.put("E", List.of(Arrays.asList("T", "E'")));
        productions.put("E'", Arrays.asList(Arrays.asList("+", "T", "E'"),
                Arrays.asList("-", "T", "E'"),
                Collections.singletonList("epsilon")));
        productions.put("T", List.of(Arrays.asList("F", "T'")));
        productions.put("T'", Arrays.asList(Arrays.asList("*", "F", "T'"),
                Arrays.asList("/", "F", "T'"),
                Collections.singletonList("epsilon")));
        productions.put("F", Arrays.asList(Arrays.asList("(", "E", ")"),
                Collections.singletonList("id"),
                Collections.singletonList("num")));

        ParseTable c = new ParseTable(firstSets, followSets, productions);
        c.generateParseTable();

        System.out.println(parse2("1+1", parseTable, "E"));
    }
}

