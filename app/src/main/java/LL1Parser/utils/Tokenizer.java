package LL1Parser.utils;

import LL1Parser.model.Tokenized;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    private static List<Tokenized> input;
    public static List<Tokenized> tokenize(List<String> stateNames, List<String> spelling) {
        input = new ArrayList<>();

        for (int i = 0; i < stateNames.size(); i++) {
            if (stateNames.get(i).equals("Comment")) {
                continue;
            }
            switch (stateNames.get(i)) {
                case "Identifier" -> input.add(new Tokenized(spelling.get(i), "identifier"));
                case "String_Literal" -> input.add(new Tokenized(spelling.get(i), "string_constant"));
                case "Integer_Literal" -> input.add(new Tokenized(spelling.get(i), "integer_constant"));
                case "Float_Literal" -> input.add(new Tokenized(spelling.get(i), "float_constant"));
                default -> input.add(new Tokenized(spelling.get(i), spelling.get(i)));
            }
        }
        return input;
    }
}