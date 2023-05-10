package LL1Parser;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Parser {
    private Hashtable<String, List<List<String>>> grammar;
    private List<String> nonTerminal;
    private Hashtable<String, List<String>> first;
    private Hashtable<String, List<String>> follow;

    private Hashtable<String, List<String>> parseTable;

    public Parser(String datFile) {
        grammar = new Hashtable<>();
        nonTerminal = new ArrayList<>();
        // TODO: read datFile and calculate FIRST & FOLLOW
    }

    private void FIRST() {
        first = new Hashtable<>();
        // TODO: this.first = calculate_first()
    }

    private void FOLLOW() {
        follow = new Hashtable<>();
        // TODO: this.follow = calculate_follow()
    }

    private void parsingTable() {
        parseTable = new Hashtable<>();
        // TODO: this.parseTable = calculate_parsingTable()
    }
}
