package functional.state;

import functional.monad.Nothing;
import functional.monad.StateM;
import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Option;

public class StateMachine<I extends Input, S> {

    Function1<I, StateM<S, Nothing>> function;

    public StateMachine(List<TransitionCondition<I, S>> transitionConditions) {


        /*
            1: function is of type: I => StateM<S, Nothing>
            2: To create StateM<S, Nothing> we:
                2a: use StateM.transition() to generate the StateM<S, Nothing>
            3: StateM.transition() takes a function with signature: S => S
                3a: Given the input I and the state S, we combine them into a Tuple2<I, S>
                3b: We then evaluate all the `transitionConditions` to find the first that
                matches the Condition.  We then use the corresponding Transition to move the
                input `state` S to a new version of the state.
            4: The resulting `function` can evaluate any `input` I against all possible
            `transitionConditions` and transition the `state` S accordingly
         */
        function = input -> StateM.transition(state ->
            Option.of(new Tuple2<>(input, state))
                .flatMap((Tuple2<I, S> inputAndState) ->
                    transitionConditions.filter(transitionAndCondition ->
                        isTransitionConditionMatch(transitionAndCondition, inputAndState)
                    )
                        .headOption()
                        .map(transitionAndCondition ->
                            transitionToNewState(transitionAndCondition, inputAndState)
                        )
                ).get()
        );
    }

    private boolean isTransitionConditionMatch(
        TransitionCondition transitionAndCondition,
        Tuple2<I, S> inputAndState
    ) {
        Condition<I, S> condition = transitionAndCondition.getCondition();
        boolean conditionMatches = condition.test(inputAndState);
        return conditionMatches;
    }

    private S transitionToNewState(
        TransitionCondition transitionAndCondition,
        Tuple2<I, S> inputAndState
    ) {
        Transition<I, S> transition = transitionAndCondition.getTransition();
        S newState = transition.apply(inputAndState);
        return newState;
    }

    /**
     * Processes each input in turn, evolving the inputs into a final State
     * @param inputs
     * @return
     */
    public StateM<S, S> process(List<I> inputs) {
        List<StateM<S, Nothing>> listOfAllStates = inputs.map(function);
        StateM<S, List<Nothing>> states = StateM.compose(listOfAllStates);
        return states.flatMap(ignored -> StateM.get());
    }
}


