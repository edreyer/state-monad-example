package bookclub;

import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functor.builtin.State;
import io.vavr.Function1;
import lombok.Value;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Id.id;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Both.both;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Take.take;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Unfoldr.unfoldr;
import static com.jnape.palatable.lambda.functor.builtin.State.state;

public class StateExercise {

    @Value
    public static class Seed{
        private int seed;
    }

    public static StateTuple<Seed, Boolean> nextBoolean(Seed seed) {
        Random r = new Random(seed.getSeed());
        int i = r.nextInt();
        return new StateTuple(new Seed(i), i % 2 == 0);
    }


    // (Seed) -> (Seed', Integer)
    // (S) -> (S', A)

    public static class RandomExercise {

        // form: (Seed) -> (Seed', Integer)
        public static Function1<Seed, StateTuple<Seed, Integer>> nextIntF = StateExercise::nextInt;

        // form: (Seed) -> (Seed', Integer)
        public interface RandomIntF extends Function1<Seed, StateTuple<Seed, Integer>> {

            default Function1<Seed, StateTuple<Seed, Boolean>> map(Function1<Integer, Boolean> f) {
                // this.apply(seed) ==> StateTuple(new seed, integer)
                // f.apply(integer)
                return (seed) -> {
                    StateTuple<Seed, Integer> newST = this.apply(seed);
                    return new StateTuple(newST.state, f.apply(newST.value));
                };
            }

            static void main(String[] args) {
                RandomIntF foo = StateExercise::nextInt;

                Function1<Seed, StateTuple<Seed, Boolean>> rndBoolF =
                    foo.map(i -> i % 2 == 0);

                StateTuple<Seed, Boolean> r1 = rndBoolF.apply(new Seed(1));
                StateTuple<Seed, Boolean> r2 = rndBoolF.apply(r1.state);
                StateTuple<Seed, Boolean> r3 = rndBoolF.apply(r2.state);
                System.out.println(r1.value + ", " + r2.value + ", " + r3.value);
            }

        }

        public interface StateF<S, A> extends Function1<S, StateTuple<S, A>> {}

        public interface RandomF<A> extends StateF<Seed, A> {}

        public static void main(String[] args) {
            // (S) -> (S', A)

            // Use RandomIntF to generate a random integer
            RandomIntF rndF = StateExercise::nextInt;
            StateTuple<Seed, Integer> r1 = rndF.apply(new Seed(0));
            StateTuple<Seed, Integer> r2 = rndF.apply(r1.state);
            // add a `map()` method to the RandomIntF interface to allow
            Function1<Seed, StateTuple<Seed, Boolean>> foo = rndF.map(i -> i % 2 == 0);
            // use map to generate a random boolean
            StateTuple<Seed, Boolean> r3 = rndF.map(i -> i % 2 == 0).apply(r2.state);


            RandomF<Integer> randF2 = StateExercise::nextInt;

            // generation of random 'things' that aren't ints

        }
    }

    @Value
    public static class StateTuple<S, A> {
        private S state;
        private A value;
    }

    // Build a functional random number generator using java.util.Random
    // Hint: Pure functions: Same input => Same output
    // (Seed) -> (Seed', Integer)
    // Function<Seed, StateTuple<Seed, Integer>>
    public static StateTuple<Seed, Integer> nextInt(Seed seed) {
        Random r = new Random(seed.getSeed());
        int i = r.nextInt();
        return new StateTuple(new Seed(i), i);
    }


    // function form: State -> (State', Value)
    // Function1<Seed, StateTuple<Seed, Boolean>>

    public interface StateF<State, Value> extends Function1<State, StateTuple<State, Value>> {
    }

    public static class StateM<State, Value> {

        private StateF<State, Value> op;

        private StateM(StateF<State, Value> op) {
            this.op = op;
        }

        public static <State, Value> StateM<State, Value> of(StateF<State, Value> op) {
            /*
            return new StateM(op);
             */
            return new StateM(op);
        }

        public static <State, Value> StateM<State, Value> of(Value a) {
            /*
            return new StateM(state -> new StateTuple(state, a));
             */
            return new StateM((state) -> new StateTuple<>(state, a));
        }

        public Value eval(State s) {
            /*
            return op.apply(s).value;
             */
            return op.apply(s).value;
        }

        public <B> StateM<State, B> flatMap(Function1<Value, StateM<State, B>> f) {
            /*
            return StateM.of((State state) -> {
                StateTuple<State, Value> tempTuple = op.apply(state);
                StateM<State, B> tempStateM = f.apply(tempTuple.value);
                StateTuple<State, B> result = tempStateM.op.apply(tempTuple.state);
                return result;
            });
             */
            return StateM.of((State state) -> {
                StateTuple<State, Value> tempTuple = op.apply(state);
                StateM<State, B> tempStateM = f.apply(tempTuple.value);
                StateTuple<State, B> result = tempStateM.op.apply(tempTuple.state);
                return result;
            });
        }

        public <B> StateM<State, B> map(Function1<Value, B> f) {
            /*
            return flatMap(value -> StateM.of(f.apply(value)));
             */
            return flatMap((value) -> StateM.of(f.apply(value)));
        }

    }

    public static void main(String[] args) {

        /////////////////
        // using 'lambda'
        /////////////////

        @Value // lombok
        class Point {
            private int x, y, z;
        }

        @Value // lombok
        class Seed{
            private int seed;
        }

        // function literal that generates new random integers (and seed')
        Fn1<Seed, Tuple2<Integer, Seed>> rndIntF =
            (Seed seed) -> {
                Random r = new Random(seed.getSeed());
                int i = r.nextInt();
                return tuple(i, new Seed(i));
            };

        // random integer monad
        State<Seed, Integer> rndIntS = state(rndIntF);

        Fn1<Integer, Tuple2<Integer, Seed>> foo = both(id(), Seed::new);

        // pointfree style
        State<Seed, Integer> rndIntS2 = state(
            both(id(), Seed::new).contraMap(s -> new Random(s.getSeed()).nextInt())
        );

        // random Point monad, created from random integer monad
        State<Seed, Point> rndPointS = rndIntS2.flatMap(x ->
            rndIntS2.flatMap(y ->
                rndIntS2.fmap(z -> new Point(x, y, z))));

        // generator for random Points.  Normally infinite, but I'm just taking 10
        // Also, lazy eval -- nothing happens yet
        Iterable<Point> points = take(10,
            unfoldr(seed -> just(rndPointS.run(seed)), new Seed(0)));

        // println() is a terminal function that causes evaluation of above
        points.forEach(System.out::println);
    }


}
