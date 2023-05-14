package LL1Parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import LL1Parser.model.Grammar;
import LL1Parser.model.Rules;
import LL1Parser.utils.FirstFollowSetsGenerator;
import LL1Parser.utils.LL1Parser;
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
        parseTable = new LinkedHashMap<>();

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

    public static boolean parse2(List<String> tokens, Map<String, Map<String, List<String>>> parseTable, String startSymbol) {
        tokens.add("$");
        Stack<String> stack = new Stack<>();
        stack.push("$");
        stack.push(startSymbol);

//        input = tokenize(input);
//        String[] tokens = input.split("\\s+");

        int i = 0;
        while (!stack.isEmpty()) {
            String top = stack.peek();
            if (top.equals("$") && i == tokens.size()) {
                return true;
            }

            String current = tokens.get(i);

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

    public static boolean parse(List<String> input) {
        Stack<String> stack = new Stack<>();
        stack.push("$");
        stack.push(start);
        input.add("$");
        int i = 0;
        while (!stack.isEmpty()) {
            String top = stack.peek();
            String currentInput = input.get(i);
            if (top.equals(currentInput)) {
                stack.pop();
                i++;
            } else if (!parseTable.containsKey(top)) {
                return false;
            } else {
                Map<String, List<String>> row = parseTable.get(top);
                if (!row.containsKey(currentInput)) {
                    return false;
                } else {
                    List<String> production = row.get(currentInput);
                    stack.pop();
                    for (int j = production.size() - 1; j >= 0; j--) {
                        String symbol = production.get(j);
                        if (!symbol.equals("epsilon")) {
                            stack.push(symbol);
                        }
                    }
                }
            }
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
        FirstFollowSetsGenerator generator = new FirstFollowSetsGenerator(grammar);
        firstSets = generator.getFirstSets();
        followSets = generator.getFollowSets();

//        for (String s : grammar.keySet()) {
//            System.out.println(s + " : " + grammar.get(s));
//        }

//        for (String s : grammar.keySet()) {
//            System.out.print(s + " -> ");
//            int c = grammar.get(s).size();
//            for (List<String> list1 : grammar.get(s)) {
//                for (String str : list1) {
//                    System.out.print(str + " ");
//                }
//                if (c > 1) {
//                    System.out.print(" | ");
//                    c--;
//                }
//            }
//            System.out.println();
//        }


        parseTbl.generateParseTable();
        parseTbl.printParseTable();



        LL1Parser ll1Parser = new LL1Parser(firstSets, followSets, grammar);
        Map<String, Map<String, List<String>>> table = ll1Parser.getParseTable();


        List<String> input = new ArrayList<>();
        File myObj = new File("app/src/main/resources/test.vc");
        Scanner myReader = null;
        try {
            myReader = new Scanner(myObj);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine().trim();
            String[] dat = data.split(" ");
            input.addAll(List.of(dat));
        }
        System.out.println(parse(input));

//        for (String s : table.keySet()) {
//            System.out.println(s + ": " + table.get(s));
//        }



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

