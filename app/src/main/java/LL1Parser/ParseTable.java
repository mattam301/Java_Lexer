package LL1Parser;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import LL1Parser.model.Grammar;
import LL1Parser.model.Rules;
import LL1Parser.utils.FirstFollowSetsGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;



public class ParseTable {

    private static Map<String, Set<String>> firstSets;
    private static Map<String, Set<String>> followSets;
    private static Map<String, List<List<String>>> grammar;
    private static Map<String, Map<String, List<String>>> parseTable;
    private static Grammar rawGrammar;
    private static Rules[] rules;
    private static List<String> terminals;
    private static List<String> nonTerminals;
    private static String start;

    public ParseTable(String datFile) {
        grammar = new LinkedHashMap<>();
        terminals = new ArrayList<>();
        nonTerminals = new ArrayList<>();
        parseTable = new HashMap<>();

        ObjectMapper mapper = new ObjectMapper();
        try {
            rawGrammar = mapper.readValue(new FileReader(datFile), Grammar.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        terminals = new ArrayList<>(rawGrammar.getTerminal());
        nonTerminals = new ArrayList<>(rawGrammar.getNonTerminal());
        start = rawGrammar.getStart();
        rules = rawGrammar.getRules();
        for (Rules rule : rules) {
            if (!grammar.containsKey(rule.getLeft())) {
                grammar.put(rule.getLeft(), Collections.singletonList(rule.getRight()));
            } else {
                List<List<String>> newGram = new ArrayList<>();
                newGram.addAll(grammar.get(rule.getLeft()));
                newGram.add(rule.getRight());
                grammar.replace(rule.getLeft(), newGram);
            }
        }
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
                                parseTable.get(nonTerminal).put(follow, List.of("epsilon"));
                            }
                        }
                    }
                } else if (!firstSymbol.equals("epsilon")) {
                    parseTable.get(nonTerminal).put(firstSymbol, production);
                } else {
                    for (String follow : followSets.get(nonTerminal)) {
                        parseTable.get(nonTerminal).put(follow, List.of("epsilon"));
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

    public static String tokenize(String input) {
        StringBuilder tokenizedInput = new StringBuilder();
        int i = 0;
        while (i < input.length()) {
            char c = input.charAt(i);
            if (Character.isDigit(c)) {
                tokenizedInput.append("num ");
                while (i < input.length() && Character.isDigit(input.charAt(i))) {
                    i++;
                }
            } else if (Character.isLetter(c)) {
                tokenizedInput.append("id ");
                while (i < input.length() && Character.isLetter(input.charAt(i))) {
                    i++;
                }
            } else {
                tokenizedInput.append(c).append(" ");
                i++;
            }
        }
        return tokenizedInput.toString().trim();
    }

    public static boolean parse2(String input, Map<String, Map<String, List<String>>> parseTable, String startSymbol) {
        input += '$';
        Stack<String> stack = new Stack<>();
        stack.push("$");
        stack.push(startSymbol);

        input = tokenize(input);
        String[] tokens = input.split("\\s+");

        int i = 0;
        while (!stack.isEmpty()) {
            String top = stack.peek();
            if (top.equals("$") && i == input.length()) {
                return true;
            }

            String current = tokens[i];

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

    public Node parse(String input, Map<String, Map<String, List<String>>> parseTable, String startSymbol) {
        Stack<Node> stack = new Stack<>();
        stack.push(new Node("$"));
        Node startNode = new Node(startSymbol);
        stack.push(startNode);

        int i = 0;
        while (!stack.isEmpty()) {
            Node top = stack.peek();
            if (top.symbol.equals("$") && i == input.length()) {
                return startNode;
            } else if (top.symbol.equals(input.charAt(i) + "")) {
                stack.pop();
                i++;
            } else if (parseTable.containsKey(top.symbol) && parseTable.get(top.symbol).containsKey(input.charAt(i) + "")) {
                stack.pop();
                List<String> production = parseTable.get(top.symbol).get(input.charAt(i) + "");
                for (int j = production.size() - 1; j >= 0; j--) {
                    if (!production.get(j).equals("epsilon")) {
                        Node child = new Node(production.get(j));
                        top.children.add(child);
                        stack.push(child);
                    }
                }
            } else {
                return null;
            }
        }

        return null;
    }



    public static void main(String[] args) {

        ParseTable parseTbl = new ParseTable("app/src/main/resources/grammar2.dat");
//        FirstFollowSetsGenerator generator = new FirstFollowSetsGenerator(grammar);
//        firstSets = generator.getFirstSets();
//        followSets = generator.getFollowSets();

        firstSets = FirstFollowSetsGenerator.calculateFirstSets(grammar, terminals);
        followSets = FirstFollowSetsGenerator.calculateFollowSets(grammar, terminals, start);

        for (String s : followSets.keySet()) {
            System.out.print(s + ": [");
            String [] str = followSets.get(s).toArray(new String[0]);
            for (int i = 0; i < str.length - 1; i++) {
                System.out.print(str[i] + ", ");
            }
//            for (String list : followSets.get(s)) {
//                System.out.print(list + ", ");
//
//            }
            System.out.println(str[str.length -1] + "]");
        }
//        parseTbl.generateParseTable();
//        parseTbl.printParseTable();


    }

    class Node {
        String symbol;
        List<Node> children;

        public Node(String symbol) {
            this.symbol = symbol;
            this.children = new ArrayList<>();
        }
    }
}

