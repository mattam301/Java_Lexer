package LL1Parser.model;

public class Tokenized {
    private String input;
    private String token;

    public Tokenized(String token) {
        this.token = token;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
