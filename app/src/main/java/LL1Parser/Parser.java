package LL1Parser;

import LL1Parser.model.Node;
import LL1Parser.model.Tokenized;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;

import java.io.*;
import java.util.*;

public class Parser {

    private static Map<String, Map<String, List<String>>> parseTable;
    private static Node startNode;
    private static String startSymbol;

    public Parser(Map<String, Map<String, List<String>>> parseTable, String startSymbol, List<Tokenized> input) {
        this.parseTable = parseTable;
        this.startSymbol = startSymbol;
        parse(input, parseTable);
        addNode(startNode);
    }

    private static Node parse(List<Tokenized> input, Map<String, Map<String, List<String>>> parseTable) {
        Stack<Node> stack = new Stack<>();
        stack.push(new Node(new Tokenized("$")));
        startNode = new Node(new Tokenized(startSymbol));
        stack.push(startNode);

        int i = 0;
        while (!stack.isEmpty()) {
            Node top = stack.peek();
            if (top.getTokenized().getToken().equals("$") && i == input.size()) {
                return startNode;
            } else if (top.getTokenized().getToken().equals(input.get(i).getToken())) {
                stack.pop();
                i++;
            } else if (parseTable.containsKey(top.getTokenized().getToken()) && parseTable.get(top.getTokenized().getToken()).containsKey(input.get(i).getToken())) {
                stack.pop();
                List<String> production = parseTable.get(top.getTokenized().getToken()).get(input.get(i).getToken());
                for (int j = production.size() - 1; j >= 0; j--) {
                    if (!production.get(j).equals("epsilon")) {
                        Node child;
                        if (!checkTerminal(parseTable.get(top.getTokenized().getToken()).get(input.get(i).getToken()).get(0))) {
                            child = new Node(new Tokenized(
                                    parseTable.get(top.getTokenized().getToken()).get(input.get(i).getToken()).get(0),
                                    production.get(j)));
                        } else {
                            child = new Node(new Tokenized(input.get(i).getInput(), production.get(j)));
                        }
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
        writer.println(nodeId + " [label=\"" + node.getTokenized().getToken() + ": " + node.getTokenized().getInput() + "\"];");
        // Recursively generate DOT code for the children of the current node
        for (Node child : node.getChildren()) {
            generateDotFile(child, writer);
            // Write an edge from the current node to its child
            String childId = "node" + System.identityHashCode(child);
            writer.println(nodeId + " -> " + childId + ";");
        }
    }

    public static Node addNode(Node node) {
        if (node == null) {
            return null;
        }
        // Recursively traverse the children of the current node
        for (Node child : node.getChildren()) {
            addNode(child);
            if (!checkTerminal(child.getTokenized().getToken()) && child.getChildren().isEmpty()) {
                Node eps = new Node(new Tokenized("epsilon", "epsilon"));
                child.getChildren().add(eps);
            }
        }

        return node;
    }

    private static boolean checkTerminal(String s) {
        if (s.charAt(0) == '<') return false;
        return true;
    }
}
