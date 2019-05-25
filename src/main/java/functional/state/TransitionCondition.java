package functional.state;

import lombok.Value;

@Value
public class TransitionCondition<I extends Input, S> {

    private Condition<I, S> condition;
    private Transition<I, S> transition;

}
