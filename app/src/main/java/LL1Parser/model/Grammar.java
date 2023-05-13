package LL1Parser.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Grammar {

    private List<String> terminal;
    private List<String> nonTerminal;
    private String start;
    private Rules[] rules;
}
