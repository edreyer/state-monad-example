package functional.state;

import lombok.Value;

/**
 * Convenience class to associate a Condition with its associated Transition
 * @param <I>
 * @param <S>
 */
@Value
public class ConditionTransition<I extends Input, S> {

    private Condition<I, S> condition;
    private Transition<I, S> transition;

}
