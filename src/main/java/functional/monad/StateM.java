package functional.monad;

import java.util.function.Function;

import cyclops.control.State;
import io.vavr.Function1;
import io.vavr.collection.List;

public class StateM<S, A> {

    public interface StateF<S,A> extends Function1<S, StateTuple<A, S>> {

        default <B> StateF<S, B> map(Function1<A, B> f) {
            return (state) ->  new StateTuple<>(f.apply(apply(state).value), state);
        }

    }

    public final StateF<S,A> run;

    public StateM(StateF<S,A> run) {
        this.run = run;
    }

    public static <S, A> StateM<S, A> of(A a) {
        return new StateM<>(state -> new StateTuple<>(a, state));
    }

    public static <S, A> StateM<S, A> of(StateF<S,A> run) {
        return new StateM<>(run);
    }

    public <B> StateM<S, B> flatMap(Function1<A, StateM<S, B>> f) {
        return StateM.of(s -> {
            StateTuple<A, S> temp = run.apply(s);
            return f.apply(temp.value).run.apply(temp.state);
        });
    }

    public <B> StateM<S, B> map(Function1<A, B> f) {
        return flatMap(a -> StateM.of(f.apply(a)));
    }

    public <B, C> StateM<S, C> map2(StateM<S, B> sb, Function<A, Function<B, C>> f) {
        return flatMap(a -> sb.map(b -> f.apply(a).apply(b)));
    }

    public static <S> StateM<S, S> get() {
        return new StateM<>(s -> new StateTuple<>(s, s));
    }

    public static <S> StateM<S, Nothing> set(S s) {
        return new StateM<>(it -> new StateTuple<>(Nothing.INSTANCE, s));
    }

    public static <S, A> StateM<S, A> getState(Function1<S, A> f) {
        return new StateM<>(s -> new StateTuple<>(f.apply(s), s));
    }

    public static <S> StateM<S, Nothing> transition(Function1<S, S> f) {
        return StateM.transition(f, Nothing.INSTANCE);
    }

    public static <S, A> StateM<S, A> transition(Function1<S, S> f, A value) {
        return new StateM<>(s -> new StateTuple<>(value, f.apply(s)));
    }

    public static <S, A> StateM<S, List<A>> compose(List<StateM<S, A>> fs) {
        return fs.foldRight(
            StateM.of(List.empty()),
            (curr, acc) -> acc.map2(curr, a -> c -> a.prepend(c))
        );
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
