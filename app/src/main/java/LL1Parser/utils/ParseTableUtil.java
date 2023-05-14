package LL1Parser.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParseTableUtil {

    private static Map<String, Map<String, List<String>>> parseTable;

    public static Map<String, Map<String, List<String>>> getParseTable(
            Map<String, List<List<String>>> grammar,
            Map<String, Set<String>> firstSets,
            Map<String, Set<String>> followSets
    ) {
        parseTable = new HashMap<>();
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
}
