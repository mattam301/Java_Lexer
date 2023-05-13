package LL1Parser.model;

import java.util.List;



public class Rules {
    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public List<String> getRight() {
        return right;
    }

    public void setRight(List<String> right) {
        this.right = right;
    }

    private String left;
    private List<String> right;
}
