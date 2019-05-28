package functional.state;

import functional.monad.StateTuple;
import io.vavr.Function1;
import io.vavr.Tuple2;

/**
 * Contains logic to transition state S to new state S', given Input
 * @param <I>
 * @param <S>
 */
public interface Transition<I extends Input, S>
    extends Function1<StateTuple<I, S>, S> {

    /**
     * Transition State S to S' using Input I
     * @param stateTuple
     * @return
     */
    @Override S apply(StateTuple<I, S> stateTuple);
}
