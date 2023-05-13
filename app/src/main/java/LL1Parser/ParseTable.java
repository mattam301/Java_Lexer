package LL1Parser;

import java.io.FileReader;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

import static LL1Parser.FirstFollowSets.*;
public class ParseTable {

    private Map<String, Set<String>> firstSets;
    private Map<String, Set<String>> followSets;
    private Map<String, List<List<String>>> grammar;
    private static Map<String, Map<String, List<String>>> parseTable;

    private List<String> terminals;
    private List<String> nonTerminals;
    private String start;

    public ParseTable(String datFile) {
        grammar = new LinkedHashMap<>();
        terminals = new ArrayList<>();
        nonTerminals = new ArrayList<>();

        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(datFile));
            JSONObject jsonObject = (JSONObject)obj;

            // get terminal characters and states
            JSONArray terminalList = (JSONArray)jsonObject.get("TERMINAL");
            Iterator iterator = terminalList.iterator();
            while (iterator.hasNext()) {
                terminals.add((String) iterator.next());
            }

            // get non-terminal characters and states
            JSONArray nonTerminalList = (JSONArray)jsonObject.get("NON_TERMINAL");
            iterator = nonTerminalList.iterator();
            while (iterator.hasNext()) {
                nonTerminals.add((String) iterator.next());
            }

            // get starting state
            start = (String)jsonObject.get("START");

            // get rules
            JSONArray grammarList = (JSONArray)jsonObject.get("RULES");
            iterator = grammarList.iterator();
            while (iterator.hasNext()) {
                JSONObject rule = (JSONObject) iterator.next();
                String left = (String) rule.get("left");
                JSONArray right = (JSONArray) rule.get("right");

                if (!grammar.containsKey(left)) {
                    grammar.put(left, Arrays.asList(right));
                } else {

                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public ParseTable(Map<String, Set<String>> firstSets, Map<String, Set<String>> followSets,
                      Map<String, List<List<String>>> grammar) {
        this.firstSets = firstSets;
        this.followSets = followSets;
        this.grammar = grammar;
        parseTable = new HashMap<>();
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

//    public static Node parse(String input, Map<String, Map<String, List<String>>> parseTable, String startSymbol) {
//        Stack<Node> stack = new Stack<>();
//        stack.push(new Node("$"));
//        Node startNode = new Node(startSymbol);
//        stack.push(startNode);
//
//        int i = 0;
//        while (!stack.isEmpty()) {
//            Node top = stack.peek();
//            if (top.symbol.equals("$") && i == input.length()) {
//                return startNode;
//            } else if (top.symbol.equals(input.charAt(i) + "")) {
//                stack.pop();
//                i++;
//            } else if (parseTable.containsKey(top.symbol) && parseTable.get(top.symbol).containsKey(input.charAt(i) + "")) {
//                stack.pop();
//                List<String> production = parseTable.get(top.symbol).get(input.charAt(i) + "");
//                for (int j = production.size() - 1; j >= 0; j--) {
//                    if (!production.get(j).equals("epsilon")) {
//                        Node child = new Node(production.get(j));
//                        top.children.add(child);
//                        stack.push(child);
//                    }
//                }
//            } else {
//                return null;
//            }
//        }
//
//        return null;
//    }
//
//    static class Node {
//        String symbol;
//        List<Node> children;
//
//        public Node(String symbol) {
//            this.symbol = symbol;
//            this.children = new ArrayList<>();
//        }
//    }
//    public static String toDot(Node root) {
//        StringBuilder dot = new StringBuilder();
//        dot.append("digraph AST {\n");
//        dot.append("node [shape=none fontname=\"Courier\" fontsize=14]\n");
//        dot.append("edge [fontname=\"Courier\" fontsize=14]\n");
//        toDot(root, dot, 0);
//        dot.append("}\n");
//        return dot.toString();
//    }
//
//    private static int toDot(Node node, StringBuilder dot, int id) {
//        int myId = id;
//        dot.append(String.format("node%d [label=\"%s\"]\n", myId, node.symbol));
//        for (Node child : node.children) {
//            int childId = toDot(child, dot, id + 1);
//            dot.append(String.format("node%d -> node%d\n", myId, id + 1));
//            id = childId;
//        }
//        return id;
//    }

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



    public static void main(String[] args) {
        ParseTable parseTable = new ParseTable("app/src/main/resources/grammar.dat");

        // Sample production rules
//        Map<String, List<List<String>>> grammar = new LinkedHashMap<>();
//        grammar.put("A", List.of(Arrays.asList("id", "A'")));
//        grammar.put("A'", Arrays.asList(Arrays.asList("=", "E"),
//                Arrays.asList("+=", "E"),
//                Arrays.asList("-=", "E"),
//                Arrays.asList("*=", "E"),
//                Arrays.asList("/=", "E")));
//
//        grammar.put("E", List.of(Arrays.asList("T", "E'")));
//        grammar.put("E'", Arrays.asList(Arrays.asList("+", "T", "E'"),
//                Arrays.asList("-", "T", "E'"),
//                Collections.singletonList("epsilon")));
//
//        grammar.put("T", List.of(Arrays.asList("F", "T'")));
//        grammar.put("T'", Arrays.asList(Arrays.asList("*", "F", "T'"),
//                Arrays.asList("/", "F", "T'"),
//                Collections.singletonList("epsilon")));
//
//        grammar.put("F", Arrays.asList(Arrays.asList("(", "E", ")"),
//                Collections.singletonList("id"),
//                Collections.singletonList("num")));

        // Sample first and follow sets
//        String startSymbol = "A";
//
//        // Calculate FIRST sets
//        Map<String, Set<String>> firstSets = calculateFirstSets(grammar);
//
//        // Calculate FOLLOW sets
//        Map<String, Set<String>> followSets = calculateFollowSets(grammar, startSymbol);
//
//        ParseTable c = new ParseTable(firstSets, followSets, grammar);
//        c.generateParseTable();

//        System.out.println(parse2("a = 1", parseTable, startSymbol));

//        String input = "1+1";
//        String tokenizedInput = tokenize(input);
//        Node ast = parse(tokenizedInput, parseTable, "E");
//        String dot = toDot(ast);
//        System.out.println(dot);
    }
}

