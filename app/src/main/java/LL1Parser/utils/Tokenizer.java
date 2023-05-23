package LL1Parser.utils;

import LL1Parser.model.Tokenized;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    private static List<Tokenized> input;
    public static List<Tokenized> tokenize(List<String> stateNames, List<String> spelling) {
        input = new ArrayList<>();

        boolean isOneLine = false;
        boolean ifFlag = false;
        boolean isNotBracketed = true;
        boolean elseFlag = false;

        for (int i = 0; i < stateNames.size(); i++) {
            if (i > 0) {
                // deal when one-line-if
                if (spelling.get(i).equals("if")) {
                    ifFlag = true;
                }
                if (ifFlag && spelling.get(i - 1).equals(")") && isNotBracketed) {
                    isOneLine = !spelling.get(i).equals("{");
                    if (isOneLine) {
                        input.add(new Tokenized("{", "{"));
                        isNotBracketed = false;
                    }
                }
                if (ifFlag && spelling.get(i - 1).equals(";") && isOneLine) {
                    input.add(new Tokenized("}", "}"));
                    ifFlag = false;
                    isOneLine = false;
                    isNotBracketed = true;
                }

                // deal when one-line-else
                if (spelling.get(i - 1).equals("else") && !spelling.get(i).equals("{")) {
                    input.add(new Tokenized("{", "{"));
                    isNotBracketed = false;
                    elseFlag = true;
                    isOneLine = true;
                }
                if (elseFlag && spelling.get(i - 1).equals(";") && isOneLine) {
                    input.add(new Tokenized("}", "}"));
                    elseFlag = false;
                    isOneLine = false;
                    isNotBracketed = true;
                }
            }

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