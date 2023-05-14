package LL1Parser.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "\\s*(//.*|/\\*.*?\\*/|\"(?:\\\\.|[^\\\\\"])*\"|'\\\\?.|\\d+\\.?\\d*|\\w+|.)"
    );

    public static List<Token> tokenize(String sourceCode) {
        List<Token> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(sourceCode);
        while (matcher.find()) {
            String lexeme = matcher.group(1).trim();
            if (!lexeme.isEmpty() && !lexeme.startsWith("//") && !lexeme.startsWith("/*")) {
                TokenType type = getTokenType(lexeme);
                tokens.add(new Token(type, lexeme));
            }
        }
        return tokens;
    }

    private static TokenType getTokenType(String lexeme) {
        if (lexeme.matches("\\d+\\.?\\d*")) {
            return TokenType.NUMBER;
        } else if (lexeme.matches("\\w+")) {
            return TokenType.IDENTIFIER;
        } else if (lexeme.matches("\"(?:\\\\.|[^\\\\\"])*\"")) {
            return TokenType.STRING;
        } else if (lexeme.matches("'\\\\?.|")) {
            return TokenType.CHARACTER;
        } else {
            return TokenType.SYMBOL;
        }
    }

    public enum TokenType {
        NUMBER,
        IDENTIFIER,
        STRING,
        CHARACTER,
        SYMBOL
    }

    public static class Token {
        private final TokenType type;
        private final String lexeme;

        public Token(TokenType type, String lexeme) {
            this.type = type;
            this.lexeme = lexeme;
        }

        public TokenType getType() {
            return type;
        }

        public String getLexeme() {
            return lexeme;
        }

        @Override
        public String toString() {
            return "Token{" +
                    "type=" + type +
                    ", lexeme='" + lexeme + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) {
        String sourceCode = "int main() {\n" +
                "    int x = 42;\n" +
                "    printf(\"x = %d\\n\", x);\n" +
                "    return 0;\n" +
                "}";

        List<Token> tokens = tokenize(sourceCode);

//        for (Token token : tokens) {
//            System.out.println(token);
//        }



    }
}
