package functional.monad;

import java.util.Objects;

import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.Function3;
import io.vavr.collection.Traversable;

import static io.vavr.API.Seq;

public class StateAPI {

    public static class ForStateM<S, T> {

        private final Traversable<StateM<S, T>> states;

        ForStateM(StateM<S, T>... states) {
            this.states = Seq(states);
        }

        public StateM<S, Nothing> yield() {
            switch (states.size()) {
                case 0:
                    throw new IllegalStateException("'states' cannot be empty");
                case 1:
                    return states.head().map(it -> Nothing.INSTANCE);
                default:
                    return states.tail().foldLeft(states.head().map(it -> Nothing.INSTANCE),
                        (acc, curr) -> acc.flatMap(a ->
                            curr.map(c -> Nothing.INSTANCE)
                        )
                    );
            }
        }

        /**
         * Yields a result for elements of the cross product of the underlying StateM.
         *
         * @param f   a function that maps an element of the cross product to a result
         * @return an {@code StateM} of mapped results
         */
        public StateM<S, T> yield(Function2<T, T, T> f) {
            Objects.requireNonNull(f, "f is null");

            switch (states.size()) {
                case 0:
                    throw new IllegalStateException("'states' cannot be empty");
                case 1:
                    return states.head();
                default:
                    return states.tail().foldLeft(states.head(),
                        (acc, curr) -> acc.flatMap(a ->
                            curr.map(c ->
                                f.apply(a, c))
                        )
                    );
            }
        }
    }

    public static class For1StateM<S1, T1> {

        private final StateM<S1, T1> ts1;

        For1StateM(StateM<S1, T1> ts1) {
            this.ts1 = ts1;
        }

        /**
         * Yields a result for elements of the cross product of the underlying StateM.
         *
         * @param f   a function that maps an element of the cross product to a result
         * @param <R> type of the resulting {@code StateM} elements
         * @return an {@code StateM} of mapped results
         */
        public <R> StateM<S1, R> yield(Function1<T1, R> f) {
            Objects.requireNonNull(f, "f is null");
            return ts1.map(f);
        }

        /**
         * A shortcut for {@code yield(Function.identity())}.
         *
         * @return an {@code Iterator} of mapped results
         */
        public StateM<S1, T1> yield() {
            return yield(Function1.identity());
        }
    }

    /**
     * For-comprehension with two Options.
     */
    public static class For2StateM<S1, T1, T2> {

        private final StateM<S1, T1> ts1;
        private final StateM<S1, T2> ts2;

        For2StateM(StateM<S1, T1> ts1, StateM<S1, T2> ts2) {
            this.ts1 = ts1;
            this.ts2 = ts2;
        }

        /**
         * Yields a result for elements of the cross product of the underlying StateMs.
         *
         * @param f   a function that maps an element of the cross product to a result
         * @param <R> type of the resulting {@code Option} elements
         * @return an {@code Option} of mapped results
         */
        public <R> StateM<S1, R> yield(Function2<? super T1, ? super T2, ? extends R> f) {
            Objects.requireNonNull(f, "f is null");
            return ts1.flatMap(t1 ->
                       ts2.map(t2 -> f.apply(t1, t2)));
        }

    }

    /**
     * For-comprehension with two Options.
     */
    public static class For3StateM<S1, T1, T2, T3> {

        private final StateM<S1, T1> ts1;
        private final StateM<S1, T2> ts2;
        private final StateM<S1, T3> ts3;

        For3StateM(StateM<S1, T1> ts1, StateM<S1, T2> ts2, StateM<S1, T3> ts3) {
            this.ts1 = ts1;
            this.ts2 = ts2;
            this.ts3 = ts3;
        }

        /**
         * Yields a result for elements of the cross product of the underlying StateMs.
         *
         * @param f   a function that maps an element of the cross product to a result
         * @param <R> type of the resulting {@code Option} elements
         * @return an {@code Option} of mapped results
         */
        public <R> StateM<S1, R> yield(Function3<? super T1, ? super T2, ? super T3, ? extends R> f) {
            Objects.requireNonNull(f, "f is null");
            return ts1.flatMap(t1 ->
                   ts2.flatMap(t2 ->
                       ts3.map(t3 -> f.apply(t1, t2, t3))));
        }

    }

    public static <S, T1> StateAPI.For1StateM<S, T1> For(StateM<S, T1> ts1) {
        Objects.requireNonNull(ts1, "ts1 is null");
        return new StateAPI.For1StateM<>(ts1);
    }

    public static <S, T1, T2> StateAPI.For2StateM<S, T1, T2> For(StateM<S, T1> ts1, StateM<S, T2> ts2) {
        Objects.requireNonNull(ts1, "ts1 is null");
        Objects.requireNonNull(ts2, "ts2 is null");
        return new StateAPI.For2StateM<>(ts1, ts2);
    }

    public static <S, T1, T2, T3> StateAPI.For3StateM<S, T1, T2, T3> For(StateM<S, T1> ts1, StateM<S, T2> ts2, StateM<S, T3> ts3) {
        Objects.requireNonNull(ts1, "ts1 is null");
        Objects.requireNonNull(ts2, "ts2 is null");
        Objects.requireNonNull(ts3, "ts3 is null");
        return new StateAPI.For3StateM<>(ts1, ts2, ts3);
    }

    public static <S, T> StateAPI.ForStateM<S, T> For(StateM<S, T> ...states) {
        Objects.requireNonNull(states, "ts1 is null");
        return new StateAPI.ForStateM<>(states);
    }



}