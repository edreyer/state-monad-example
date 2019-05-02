package functional.monad;

import java.util.function.Function;

import io.vavr.Tuple2;

public class StateM<S, A> {

    public interface StateF<S,A> extends Function<S, Tuple2<A, S>> {}

    public final StateF<S,A> run;

    public StateM(StateF<S,A> run) {
        this.run = run;
    }

    public static <S, A> StateM<S, A> of(A a) {
        return new StateM<>(state -> new Tuple2<>(a, state));
    }

    public <B> StateM<S, B> flatMap(Function<A, StateM<S, B>> f) {
        return new StateM<>(s -> {
            Tuple2<A, S> temp = run.apply(s);
            return f.apply(temp._1).run.apply(temp._2);
        });
    }

    public <B> StateM<S, B> map(Function<A, B> f) {
        return flatMap(a -> StateM.of(f.apply(a)));
    }

    public static <S> StateM<S, S> get() {
        return new StateM<>(s -> new Tuple2<>(s, s));
    }

    public static <S> StateM<S, Nothing> set(S s) {
        return new StateM<>(it -> new Tuple2<>(Nothing.INSTANCE, s));
    }

    public static <S, A> StateM<S, A> getState(Function<S, A> f) {
        return new StateM<>(s -> new Tuple2<>(f.apply(s), s));
    }

    public static <S> StateM<S, Nothing> transition(Function<S, S> f) {
        return new StateM<>(s -> new Tuple2<>(Nothing.INSTANCE, f.apply(s)));
    }

    public static <S, A> StateM<S, A> transition(Function<S, S> f, A value) {
        return new StateM<>(s -> new Tuple2<>(value, f.apply(s)));
    }

    public Tuple2<A, S> apply(S s) {
        return run.apply(s);
    }

    public A eval(S s) {
        return apply(s)._1;
    }


}
