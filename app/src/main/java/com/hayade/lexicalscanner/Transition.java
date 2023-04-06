package com.hayade.lexicalscanner;

import java.util.regex.Pattern;

/**
 * Transition.
 */
public class Transition {

    private Pattern pattern;
    private State nextState;

    /**
     * Transition Constructor.
     *
     * @param matcher matcher to check next character.
     * @param nextState next state when next character matches this transition input.
     */
    public Transition(String matcher, State nextState) {
        pattern = Pattern.compile(matcher);
        this.nextState = nextState;
    }

    /**
     * Check whether next character is match with transition input or not.
     *
     * @param c next character.
     * @return next character is match with transition input.
     */
    public boolean match(char c) {
        return pattern.matcher(Character.toString(c)).matches();
    }

    public State getNextState() {
        return nextState;
    }

}
