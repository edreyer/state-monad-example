package functional.state;

import java.util.function.Function;

import functional.monad.Nothing;
import functional.monad.StateM;
import functional.monad.StateTuple;
import io.vavr.Function1;
import io.vavr.collection.List;
import io.vavr.control.Option;

public class StateMachine<I extends Input, S> {

    Function1<I, StateM<S, Nothing>> function;

    public StateMachine(List<ConditionTransition<I, S>> transitionConditions) {

        function = input -> StateM.transition(state ->
            Option.of(new StateTuple<>(input, state))
                .flatMap(getStateTransitionFunction(transitionConditions))
                .get()
        );

    }

    /**
     * Returns a function that finds the correct state transition to apply
     * @param transitionConditions
     * @return
     */
    private Function<StateTuple<I, S>, Option<? extends S>> getStateTransitionFunction(
        List<ConditionTransition<I, S>> transitionConditions) {

        return (StateTuple<I, S> inputAndState) -> transitionConditions
                .filter(transitionAndCondition ->
                    // applies to this condition?
                    transitionAndCondition.getCondition().test(inputAndState)
                )
                .headOption()
                .map(transitionAndCondition ->
                    // transition state to state'
                    transitionAndCondition.getTransition().apply(inputAndState)
                );
    }

    /**
     * Processes each input in turn, evolving the inputs into a final State
     * @param inputs
     * @return
     */
    public StateM<S, S> process(List<I> inputs) {
        List<StateM<S, Nothing>> listOfAllStates = inputs.map(function);
        StateM<S, List<Nothing>> composedState = StateM.compose(listOfAllStates);
        return composedState.flatMap(ignored -> StateM.get());
    }
}


