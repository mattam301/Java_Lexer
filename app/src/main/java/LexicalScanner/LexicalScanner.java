package LexicalScanner;

import LL1Parser.Constant;
import LL1Parser.Parser;
import LL1Parser.utils.ParseTableUtil;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.io.IOException;

import static LL1Parser.utils.Tokenizer.tokenize;

public class LexicalScanner {

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s");
    private static final Pattern VERTICAL_WHITESPACE_PATTERN = Pattern.compile("\\v");

    private final State initialState;

    private static List<String> keywords;

    /**
     * Constructor of Lexical Scanner.
     *
     * @param input input stream of automaton file.
     */
    public LexicalScanner(InputStream input) {
        Scanner scanner = new Scanner(input);

        int totalStates = scanner.nextInt();
        int initialStateIndex = scanner.nextInt();
        scanner.nextLine();

        // Get all end states
        String endStatesLine = scanner.nextLine();
        int[] endStates = Stream.of(endStatesLine.split("\\s")).mapToInt(Integer::parseInt).sorted()
                .toArray();

        // Get all end states' name
        String endStatesNamesLine = scanner.nextLine();
        String[] endStatesNames = endStatesNamesLine.split("\\s");

        // Get all states that have next states
        String haveNextStatesLine = scanner.nextLine();
        int[] haveNextStates = Stream.of(haveNextStatesLine.split("\\s"))
                .mapToInt(Integer::parseInt).sorted().toArray();

        // Get all keywords
        String keywordsLine = scanner.nextLine();
        keywords = List.of(keywordsLine.split("\\s"));

        // Init state list
        State[] states = new State[totalStates];
        Arrays.setAll(states, i -> {
            int endStateIndex = Arrays.binarySearch(endStates, i);
            boolean isEnd = endStateIndex >= 0;
            boolean haveNextState = Arrays.binarySearch(haveNextStates, i) >= 0;
            String stateName = isEnd ? endStatesNames[endStateIndex] : "Invalid";
            int stateIdx = i;
            return new State(isEnd, haveNextState, stateName, stateIdx);
        });
        initialState = states[initialStateIndex];

        // Parse input for state transition
        String[] regexs = scanner.nextLine().trim().split("\\s+");
        for (State state : states) {
            if (state.haveNextState()) {
                for (String regex : regexs) {
                    int stateIndex = scanner.nextInt();
                    // Add transition if next state is available
                    if (stateIndex >= 0) {
                        state.addTransition(new Transition(regex, states[stateIndex]));
                    }
                }
            }
        }
        scanner.close();
    }

    /**
     * Scan, tokenize input string and print out tokens.
     *
     * @param input input stream.
     * @param output output stream.
     * @throws IOException scan error.
     */
    public void scan(InputStream input, OutputStream output, String filename) throws IOException {
        PushbackReader reader = new PushbackReader(new InputStreamReader(input));

        State state = this.initialState;
        StringBuilder token = new StringBuilder();
        List<String> tokens = new ArrayList<>();
        List<String> stateNames = new ArrayList<>();
        List<String> spelling = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int line = 1;
        int pos = 0;

        for (int nextChar = reader.read(); nextChar != -1; nextChar = reader.read()) {
            char c = (char) nextChar;
            ++pos;
            State nextState = state.nextState(c);
            if (nextState != null) {
                token.append(c);
                state = nextState;
            } else {
                handleNoNextState(state, token.toString(), c, line, pos, tokens, stateNames, spelling, errors);
                token.setLength(0);
                if (state != this.initialState) {
                    reader.unread(c);
                    --pos;
                    state = this.initialState;
                    continue;
                }
            }
            if (isLineTerminator(c, reader)) {
                ++line;
                pos = 0;
            }
        }
        if (token.length() > 0) {
            handleNoNextState(state, token.toString(), Character.MIN_VALUE, line, pos, tokens,stateNames, spelling, errors);
        }

//        toOutput(System.out, tokens, errors);
        toOutput(output, tokens, errors);
        toVctok(filename, spelling);

        Parser parser = new Parser(
                ParseTableUtil.generateParseTable(Constant.grammarPath),
                ParseTableUtil.getStart(), tokenize(stateNames, spelling));
        String fileName = "ast.dot";
        parser.generateDotFile(fileName);
        parser.generateTreePng(fileName);


        reader.close();
    }

    /**
     * When transition is not exist for pair of <code>state</code> and <code>nextChar</code>,
     * determine whether this <code>state</code> is ending state or not and act accordingly.
     */
    private void handleNoNextState(State state, String token, char nextChar, int line, int pos,
                                   List<String> tokens, List<String> stateNames, List<String> spelling, List<String> errors) {
        if (state.isEnd()) {
            String beautifiedToken = token.replace("\n", "\\n");

            String stateName = state.getStateName();
            if (keywords.contains(beautifiedToken)) {
                stateName = "Keyword";
            }
            int stateIdx = state.getStateIdx();
            tokens.add(String.format("Kind = %d [%s], spelling = \"%s\", position = %d(%d)..%d(%d)",
                    stateIdx, stateName, beautifiedToken, line, pos - beautifiedToken.length(), line, pos - 1));
            stateNames.add(stateName);
            spelling.add(beautifiedToken);

        } else if (!(match(nextChar, WHITESPACE_PATTERN) && state == initialState)) {
            String c = match(nextChar, VERTICAL_WHITESPACE_PATTERN) ? "newline"
                    : Character.toString(nextChar);
            errors.add(String.format("Error[Ln %d]: current string is: '%s', but next char is: '%s'",
                    line, token, c));
        }
    }

    /**
     * Recognized line terminators:
     * <ul>
     * <li>A newline (line feed) character ('\n'),
     * <li>A carriage-return character followed immediately by a newline character ("\r\n"),
     * <li>A standalone carriage-return character ('\r'),
     * <li>A next-line character ('\u0085'),
     * <li>A line-separator character ('\u2028'), or
     * <li>A paragraph-separator character ('\u2029').
     * </ul>
     */
    private boolean isLineTerminator(char currentChar, PushbackReader reader) throws IOException {
        if (currentChar == '\r') {
            int c = reader.read();
            if (c == -1 || (char) c == '\n') {
                return true;
            }
            reader.unread(c);
            return true;
        }
        return match(currentChar, VERTICAL_WHITESPACE_PATTERN);
    }

    /** Match <code>c</code> against pattern <code>pattern</code>. */
    private boolean match(char c, Pattern pattern) {
        return pattern.matcher(Character.toString(c)).matches();
    }

    private void toOutput(OutputStream output, List<String> tokens, List<String> errors) {
        PrintStream printStream = new PrintStream(output);

        printStream.println("Tokens:");
        tokens.forEach(printStream::println);
        if(errors.size() != 0){
            printStream.println("\nErrors:");
            errors.forEach(printStream::println);
        } else{
            printStream.println("\nThere's no errors !!!");
        }
        printStream.close();
    }

    private void toVctok(String filename, List<String> spelling) throws IOException {
        // String outputPath =
        //         Objects.requireNonNull(App.class.getResource("")).getFile()
        //                 + filename + ".vctok";

        String outputPath = filename + ".vctok";
        System.out.println("\t\t\t\t\t\t\t" + outputPath);
        File outputFile = new File(outputPath);
        outputFile.createNewFile();
        OutputStream outputStream = new FileOutputStream(outputFile);

        PrintStream printStream = new PrintStream(outputStream);

        spelling.forEach(printStream::println);

        printStream.close();
    }
}
