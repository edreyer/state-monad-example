package functional.state;

import functional.monad.StateTuple;

import java.util.function.Function;

public interface Transition<I, S> extends Function<StateTuple<I, S>, S> {}
