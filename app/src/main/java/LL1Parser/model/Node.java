package LL1Parser.model;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private Tokenized symbol;

    private List<Node> children;

    public Node(Tokenized symbol) {
        this.symbol = symbol;
        this.children = new ArrayList<>();
    }

    public Tokenized getTokenized() {
        return symbol;
    }

    public void setTokenized(Tokenized symbol) {
        this.symbol = symbol;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }


}
