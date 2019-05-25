package functional.state;

import java.util.function.Predicate;

import io.vavr.Tuple2;

public interface Condition<I extends Input, S> extends Predicate<Tuple2<I, S>> {}
