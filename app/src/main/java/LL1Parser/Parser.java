package LL1Parser;

import LL1Parser.model.Node;
import LL1Parser.utils.ParseTableUtil;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;

import java.io.*;
import java.util.*;

public class Parser {

    private static Map<String, Map<String, List<String>>> parseTable;
    private static Node startNode;
    private static String startSymbol;

    public Parser(Map<String, Map<String, List<String>>> parseTable, String startSymbol, List<String> input) {
        this.parseTable = parseTable;
        this.startSymbol = startSymbol;
        parse(input, parseTable);
    }

    private static Node parse(List<String> input, Map<String, Map<String, List<String>>> parseTable) {
        Stack<Node> stack = new Stack<>();
        stack.push(new Node("$"));
        startNode = new Node(startSymbol);
        stack.push(startNode);

        int i = 0;
        while (!stack.isEmpty()) {
            Node top = stack.peek();
            if (top.getSymbol().equals("$") && i == input.size()) {
                return startNode;
            } else if (top.getSymbol().equals(input.get(i))) {
                stack.pop();
                i++;
            } else if (parseTable.containsKey(top.getSymbol()) && parseTable.get(top.getSymbol()).containsKey(input.get(i))) {
                stack.pop();
                List<String> production = parseTable.get(top.getSymbol()).get(input.get(i));
                for (int j = production.size() - 1; j >= 0; j--) {
                    if (!production.get(j).equals("epsilon")) {
                        Node child = new Node(production.get(j));
                        top.getChildren().add(child);
                        stack.push(child);
                    }
                }
            } else {
                return null;
            }
        }

        return null;
    }

    public static void generateDotFile(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("digraph AST {");
            generateDotFile(startNode, writer);
            writer.println("}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateTreePng(String filePath) {
        MutableGraph graph;
        try {
            graph = new guru.nidi.graphviz.parse.Parser().read(new File(filePath));
            String pngFileName = "ast.png";
            Graphviz.fromGraph(graph).render(Format.PNG).toFile(new File(pngFileName));
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    private static void generateDotFile(Node node, PrintWriter writer) {
        if (node == null) {
            return;
        }
        // Generate a unique identifier for the current node
        String nodeId = "node" + System.identityHashCode(node);
        // Write the current node to the DOT file
        writer.println(nodeId + " [label=\"" + node.getSymbol() + "\"];");
        // Recursively generate DOT code for the children of the current node
        for (Node child : node.getChildren()) {
            generateDotFile(child, writer);
            // Write an edge from the current node to its child
            String childId = "node" + System.identityHashCode(child);
            writer.println(nodeId + " -> " + childId + ";");
        }
    }

    public static String addParentheses(Node node) {
        if (node == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (node.getChildren().size() == 0) {
            // Leaf node
            sb.append(node.getSymbol());
        } else if (node.getChildren().size() == 1) {
            // Unary operator
            sb.append(node.getSymbol());
            sb.append(addParentheses(node.getChildren().get(0)));
        } else {
            // Binary operator
            boolean addParens = false;
            for (Node child : node.getChildren()) {
                if (containsSymbol(child, "integer_constant") || containsSymbol(child, "float_constant")) {
                    addParens = true;
                    break;
                }
            }
            if (addParens) {
                sb.append("(");
            }
            sb.append(addParentheses(node.getChildren().get(0)));
            sb.append(" ");
            sb.append(node.getSymbol());
            sb.append(" ");
            sb.append(addParentheses(node.getChildren().get(1)));
            if (addParens) {
                sb.append(")");
            }
        }
        return sb.toString();
    }

    private static boolean containsSymbol(Node node, String symbol) {
        if (node == null) {
            return false;
        }
        if (node.getSymbol().equals(symbol)) {
            return true;
        }
        for (Node child : node.getChildren()) {
            if (containsSymbol(child, symbol)) {
                return true;
            }
        }
        return false;
    }
}
