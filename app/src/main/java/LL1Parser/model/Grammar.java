package LL1Parser.model;

import java.util.List;

public class Grammar {
    private List<String> terminal;
    private List<String> nonTerminal;
    private String start;

    public List<String> getTerminal() {
        return terminal;
    }

    public void setTerminal(List<String> terminal) {
        this.terminal = terminal;
    }

    public List<String> getNonTerminal() {
        return nonTerminal;
    }

    public void setNonTerminal(List<String> nonTerminal) {
        this.nonTerminal = nonTerminal;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public Rules[] getRules() {
        return rules;
    }

    public void setRules(Rules[] rules) {
        this.rules = rules;
    }

    private Rules[] rules;
}
