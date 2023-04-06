

import java.util.ArrayList;
import java.util.List;

/**
 * State of Transition Graph.
 */
public class State {

    private boolean isEnd;
    private boolean haveNextState;
    private String stateName;
    private int stateIdx;
    private List<Transition> transitions = new ArrayList<>();

    /**
     * State Constructor.
     *
     * @param isEnd If the current state is an end state
     * @param haveNextState If the current state have edges going out
     * @param stateName The textual name of the state
     */
    public State(boolean isEnd, boolean haveNextState, String stateName, int stateIdx) {
        this.isEnd = isEnd;
        this.haveNextState = haveNextState;
        this.stateName = stateName;
        this.stateIdx = stateIdx;
    }

    /**
     * Get next state with input character.
     *
     * @param c input character.
     * @return next state in transition graph or <code>null</code> if next state is unavailable.
     */
    public State nextState(char c) {
        Transition transition =
                transitions.stream().filter(t -> t.match(c)).findFirst().orElse(null);
        return transition != null ? transition.getNextState() : null;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public boolean haveNextState() {
        return haveNextState;
    }

    public String getStateName() {
        return stateName;
    }

    public void addTransition(Transition transition) {
        transitions.add(transition);
    }

    public int getStateIdx() {
        return stateIdx;
    }
}
