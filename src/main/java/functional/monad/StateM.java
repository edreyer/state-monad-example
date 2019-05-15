package functional.monad;

import java.util.function.Function;

public class StateM<S, A> {

    public interface StateF<S,A> extends Function<S, StateTuple<A, S>> {}

    public final StateF<S,A> run;

    public StateM(StateF<S,A> run) {
        this.run = run;
    }

    public static <S, A> StateM<S, A> of(A a) {
        return new StateM<>(state -> new StateTuple<>(a, state));
    }

    public <B> StateM<S, B> flatMap(Function<A, StateM<S, B>> f) {
        return new StateM<>(s -> {
            StateTuple<A, S> temp = run.apply(s);
            return f.apply(temp.value).run.apply(temp.state);
        });
    }

    public <B> StateM<S, B> map(Function<A, B> f) {
        return flatMap(a -> StateM.of(f.apply(a)));
    }

    public static <S> StateM<S, S> get() {
        return new StateM<>(s -> new StateTuple<>(s, s));
    }

    public static <S> StateM<S, Nothing> set(S s) {
        return new StateM<>(it -> new StateTuple<>(Nothing.INSTANCE, s));
    }

    public static <S, A> StateM<S, A> getState(Function<S, A> f) {
        return new StateM<>(s -> new StateTuple<>(f.apply(s), s));
    }

    public static <S> StateM<S, Nothing> transition(Function<S, S> f) {
        return new StateM<>(s -> new StateTuple<>(Nothing.INSTANCE, f.apply(s)));
    }

    public static <S, A> StateM<S, A> transition(Function<S, S> f, A value) {
        return new StateM<>(s -> new StateTuple<>(value, f.apply(s)));
    }

    public StateTuple<A, S> apply(S s) {
        return run.apply(s);
    }

    public S evalState(S s) {
        return apply(s).state;
    }

    public A eval(S s) {
        return apply(s).value;
    }


}
