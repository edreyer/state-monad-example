package functional.state;

import functional.monad.StateTuple;

import java.util.function.Predicate;

public interface Condition<I, S> extends Predicate<StateTuple<I, S>> {}
