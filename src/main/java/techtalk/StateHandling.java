package techtalk;

import java.util.Random;
import java.util.function.Function;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functor.builtin.State;
import lombok.Builder;
import lombok.Value;
import techtalk.StateHandling.Step1.Seed;
import techtalk.StateHandling.Step2.Point;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Take.take;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Unfoldr.unfoldr;
import static com.jnape.palatable.lambda.functor.builtin.State.state;
import static java.util.Arrays.asList;

public class StateHandling {

    static class Step1 {
        @Value
        static class Seed {
            Integer seed;
        }

        @Value
        @Builder
        static class RandomIntResult {
            Integer rand;
            Seed seedPrime;
        }

        static RandomIntResult randomInteger(Seed seed) {
            Random rand = new Random(seed.seed);
            int x = rand.nextInt();
            return RandomIntResult.builder()
                .seedPrime(new Seed(x))
                .rand(x)
                .build();
        }

        interface RandomIntFunction extends Function<Seed, RandomIntResult> {}

        static final RandomIntFunction randomIntFn = Step1::randomInteger;

        public static void main(String ...args) {
            RandomIntResult r1 = randomIntFn.apply(new Seed(0));
            RandomIntResult r2 = randomIntFn.apply(r1.seedPrime);
            RandomIntResult r3 = randomIntFn.apply(r2.seedPrime);

            asList(r1.rand, r2.rand, r3.rand).forEach(System.out::println);
        }

    }

    static class Step2 {

        @Value
        @Builder
        static class StateResult<VALUE, STATE> {
            VALUE value;
            STATE state;
        }

        @Value // lombok
        static class Point {
            private int x, y, z;
        }

        static StateResult<Integer, Seed> randomInteger(Seed seed) {
            Random rand = new Random(seed.seed);
            int x = rand.nextInt();
            return StateResult.<Integer, Seed>builder()
                .value(x)
                .state(new Seed(x))
                .build();
        }

        // S -> (V, S')
        interface StateFn<STATE, VALUE> extends Function<STATE, StateResult<VALUE, STATE>> {
            // map() allows us to use transform a generator function to produce B's instead of A's
            default <V2> StateFn<STATE, V2> map(Function<VALUE, V2> mapper) {
                return (STATE state) -> {
                    StateResult<VALUE, STATE> result = this.apply(state);
                    return StateResult.<V2, STATE>builder()
                        .state(result.state)
                        .value(mapper.apply(result.value))
                        .build();
                };
            }
        }

        static StateFn<Seed, Integer>   randInt =       Step2::randomInteger;
        static StateFn<Seed, Float>     randFloat =     randInt.map(x -> x * 1.0f);
        static StateFn<Seed, Boolean>   randBoolean =   randInt.map(x -> x % 2 == 0);

        public static void main(String[] args) {
            StateResult<Boolean, Seed> r1 = randBoolean.apply(new Seed(0));
            StateResult<Boolean, Seed> r2 = randBoolean.apply(r1.state);
            StateResult<Boolean, Seed> r3 = randBoolean.apply(r2.state);

            asList(r1, r2, r3).forEach(r -> System.out.println(r.value));

            Function<Seed, Function<Seed, Function<Seed, Point>>> randPoint =
                randInt.andThen(a ->
                randInt.andThen(b ->
                randInt.andThen(c ->
                    new Point(a.value, b.value, c.value)
                )));

            // Problem: we have to supply the state each time
            Point point = randPoint
                .apply(new Seed(0))
                .apply(new Seed(0))
                .apply(new Seed(0));

            System.out.println(point);
        }
    }

    static class Step3 {

        static Fn1<Seed, Tuple2<Integer, Seed>> rndIntF = (Seed seed) -> {
            Random r = new Random(seed.getSeed());
            int i = r.nextInt();
            return tuple(i, new Seed(i));
        };

        static State<Seed, Integer> rndIntS = state(rndIntF);

        static State<Seed, Point> rndPointS =
            rndIntS.flatMap(x ->
            rndIntS.flatMap(y ->
            rndIntS.fmap(z -> new Point(x, y, z))));

        public static void main(String[] args) {
            // generator for random Points.  Normally infinite, but I'm just taking 10
            // Also, lazy eval -- nothing happens yet
            Iterable<Point> points =
                take(10,
                unfoldr(seed ->
                just(rndPointS.run(seed)), new Seed(0))
            );

            points.forEach(System.out::println);
        }

    }

}
