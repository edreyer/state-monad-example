package functional.examples;

import java.util.Random;
import java.util.function.Function;

import functional.monad.StateM;
import io.vavr.Tuple2;
import lombok.Value;

public class RandomExamples {

    @Value
    public static class Seed {
        private int seed;
    }

    private static Tuple2<Integer, Seed> nextInt(Seed seed) {
        Random r = seed == null ? new Random() : new Random(seed.getSeed());
        int i = r.nextInt();
        return new Tuple2<>(i, new Seed(i));
    }

    /*
     * Initial implementation using initial concrete APIs
     */

    public static class Generator1 {
        // Concrete API
        public static Tuple2<Integer, Seed> nextInt(Seed seed) {
            return RandomExamples.nextInt(seed);
        }
    }

    /*
     * First evolution.  Generalizes the static method `Generator1.nextInt(RNG)` into a Function.
     */

    public interface RandomF<A> extends Function<Seed, Tuple2<A, Seed>> {}

    public static class Generator2 {
        // Slightly abstracted into Function<T,R>
        public static RandomF<Integer> nextInt = RandomExamples::nextInt;
    }

    /*
     * Final stage using discovered StateM<S,A> Monad.
     */

    public static class RandomS<A> extends StateM<Seed, A> {

        private RandomS(StateF<Seed, A> run) {
            super(run);
        }

        public static RandomS<Integer> intRnd = new RandomS<>(RandomExamples::nextInt);

    }

    public static class Generator3 {
        // Fully abstracted into StateM<S, A>
        public static RandomS<Integer> nextInt = RandomS.intRnd;
    }

}
