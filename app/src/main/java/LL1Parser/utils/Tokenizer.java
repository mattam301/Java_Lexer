package LL1Parser.utils;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    private static List<String> input;
    public static List<String> tokenize(List<String> stateNames, List<String> spelling) {
        input = new ArrayList<>();

        for (int i = 0; i < stateNames.size(); i++) {
            if (stateNames.get(i).equals("Comment")) {
                continue;
            }
            switch (stateNames.get(i)) {
                case "Identifier" -> input.add("identifier");
                case "String_Literal" -> input.add("string_constant");
                case "Integer_Literal" -> input.add("integer_constant");
                case "Float_Literal" -> input.add("float_constant");
                default -> input.add(spelling.get(i));
            }
        }
        return input;
    }
}
