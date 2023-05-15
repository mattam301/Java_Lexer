package LL1Parser.model;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private String symbol;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    private List<Node> children;

    public Node(String symbol) {
        this.symbol = symbol;
        this.children = new ArrayList<>();
    }


}
