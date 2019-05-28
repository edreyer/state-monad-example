package functional.state;

import java.util.function.Predicate;

import functional.monad.StateTuple;
import io.vavr.Tuple2;

/**
 * Predicate to determine whether a given Input matches
 * @param <I>   State machine Input
 * @param <S>   State machine state object (can be any @Value object)
 */
public interface Condition<I extends Input, S>
    extends Predicate<StateTuple<I, S>> {

    /**
     * Test to see if Input I applies to state S
     * @param stateTuple
     * @return
     */
    @Override boolean test(StateTuple<I, S> stateTuple);
}
