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


    public static Node parse(List<String> input, Map<String, Map<String, List<String>>> parseTable, String startSymbol) {
        Stack<Node> stack = new Stack<>();
        stack.push(new Node("$"));
        Node startNode = new Node(startSymbol);
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

    private static void generateDotFile(Node root, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("digraph AST {");
            generateDotFile(root, writer);
            writer.println("}");
        } catch (IOException e) {
            e.printStackTrace();
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

    public static void main(String[] args) {
        parseTable = ParseTableUtil.generateParseTable(Constant.grammarPath);
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
        parseTable = ParseTableUtil.generateParseTable("app/src/main/resources/grammar2.dat");

        String dotFileName = "ast.dot";
        generateDotFile(parse(input, parseTable, "<program>"), dotFileName);

        MutableGraph graph;
        try {
            graph = new guru.nidi.graphviz.parse.Parser().read(new File(dotFileName));
            String pngFileName = "ast.png";
            Graphviz.fromGraph(graph).render(Format.PNG).toFile(new File(pngFileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
