package functional.state;

import functional.monad.Nothing;
import functional.monad.StateM;

import java.util.function.Function;

public class StateMachine<I, S> {

    Function<I, StateM<S, Nothing>> function;

//    public StateMachine(List<Tuple2<Condition<I, S>, Transition<I, S>>> transitions) {
//        function = i -> StateM.transition(m ->
//            Optional.of(new StateTuple<>(i, m)).flatMap((StateTuple<I, S> t) ->
//                transitions.filter((Tuple2<Condition<I, S>, Transition<I, S>> x) ->
//                    x._1.test(t)).findFirst().map((Tuple2<Condition<I, S>, Transition<I, S>> y) ->
//                    y._2.apply(t))).get());
//    }
//
//    public StateM<S, S> process(List<I> inputs) {
//        List<StateM<S, Nothing>> a = inputs.map(function);
//        StateM<S, List<Nothing>> b = StateM.compose(a);
//        return b.flatMap(x -> StateM.get());
//    }
}


