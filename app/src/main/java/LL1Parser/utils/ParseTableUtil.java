package LL1Parser.utils;

import LL1Parser.model.Grammar;
import LL1Parser.model.Rules;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ParseTableUtil {

    private static Map<String, Set<String>> firstSets;
    private static Map<String, Set<String>> followSets;
    private static Map<String, List<List<String>>> grammar;
    private static Grammar rawGrammar;
    private static Rules[] rules;
    private static List<String> terminals;
    private static List<String> nonTerminals;
    private static String start;
    private static Map<String, Map<String, List<String>>> parseTable;

    public static Map<String, Map<String, List<String>>> generateParseTable(String datFile) {
        initialize(datFile);
        parseTable = new LinkedHashMap<>();
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
        return parseTable;
    }

    private static void initialize(String datFile) {
        grammar = new LinkedHashMap<>();
        terminals = new ArrayList<>();
        nonTerminals = new ArrayList<>();
        parseTable = new LinkedHashMap<>();

        ObjectMapper mapper = new ObjectMapper();
        try {
            rawGrammar = mapper.readValue(new FileReader(datFile), Grammar.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        terminals = new ArrayList<>(rawGrammar.getTerminal());
        nonTerminals = new ArrayList<>(rawGrammar.getNonTerminal());
        start = rawGrammar.getStart();
        rules = rawGrammar.getRules();
        for (Rules rule : rules) {
            if (!grammar.containsKey(rule.getLeft())) {
                grammar.put(rule.getLeft(), Collections.singletonList(rule.getRight()));
            } else {
                List<List<String>> newGram = new ArrayList<>();
                newGram.addAll(grammar.get(rule.getLeft()));
                newGram.add(rule.getRight());
                grammar.replace(rule.getLeft(), newGram);
            }
        }
        FirstFollowSetsGenerator generator = new FirstFollowSetsGenerator(grammar);
        firstSets = generator.getFirstSets();
        followSets = generator.getFollowSets();
    }

    public static Map<String, Set<String>> getFirstSets() {
        return firstSets;
    }

    public static void setFirstSets(Map<String, Set<String>> firstSets) {
        ParseTableUtil.firstSets = firstSets;
    }

    public static Map<String, Set<String>> getFollowSets() {
        return followSets;
    }

    public static void setFollowSets(Map<String, Set<String>> followSets) {
        ParseTableUtil.followSets = followSets;
    }

    public static Map<String, List<List<String>>> getGrammar() {
        return grammar;
    }

    public static void setGrammar(Map<String, List<List<String>>> grammar) {
        ParseTableUtil.grammar = grammar;
    }

    public static Grammar getRawGrammar() {
        return rawGrammar;
    }

    public static void setRawGrammar(Grammar rawGrammar) {
        ParseTableUtil.rawGrammar = rawGrammar;
    }

    public static Rules[] getRules() {
        return rules;
    }

    public static void setRules(Rules[] rules) {
        ParseTableUtil.rules = rules;
    }

    public static List<String> getTerminals() {
        return terminals;
    }

    public static void setTerminals(List<String> terminals) {
        ParseTableUtil.terminals = terminals;
    }

    public static List<String> getNonTerminals() {
        return nonTerminals;
    }

    public static void setNonTerminals(List<String> nonTerminals) {
        ParseTableUtil.nonTerminals = nonTerminals;
    }

    public static String getStart() {
        return start;
    }

    public static void setStart(String start) {
        ParseTableUtil.start = start;
    }

    public static Map<String, Map<String, List<String>>> getParseTable() {
        return parseTable;
    }

    public static void setParseTable(Map<String, Map<String, List<String>>> parseTable) {
        ParseTableUtil.parseTable = parseTable;
    }
}
