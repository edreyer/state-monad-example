package functional.state;

import io.vavr.Function1;
import io.vavr.Tuple2;

public interface Transition<I extends Input, S> extends Function1<Tuple2<I, S>, S> {}
