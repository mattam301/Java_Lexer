package LL1Parser.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Grammar {

    private List<String> terminal;

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

    private List<String> nonTerminal;
    private String start;
    private Rules[] rules;
}