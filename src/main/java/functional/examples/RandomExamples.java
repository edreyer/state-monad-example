package functional.examples;

import functional.monad.StateM;
import functional.monad.StateTuple;
import lombok.Value;

import java.util.Random;
import java.util.function.Function;

public class RandomExamples {

    @Value
    public static class Seed {
        private int seed;
    }

    private static StateTuple<Integer, Seed> nextInt(Seed seed) {
        Random r = seed == null ? new Random() : new Random(seed.getSeed());
        int i = r.nextInt();
        return new StateTuple<>(i, new Seed(i));
    }

    /*
     * Initial implementation using initial concrete APIs
     */

    public static class Generator1 {
        // Concrete API
        public static StateTuple<Integer, Seed> nextInt(Seed seed) {
            return RandomExamples.nextInt(seed);
        }
    }

    /*
     * First evolution.  Generalizes the static method `Generator1.nextInt(RNG)` into a Function.
     */

    public interface RandomF<A> extends Function<Seed, StateTuple<A, Seed>> {}

    public static class Generator2 {
        // Slightly abstracted into Function<T,R>
        public static RandomF<Integer> nextInt = RandomExamples::nextInt;
    }

    /*
     * Final stage using discovered StateM<S,A> Monad.
     */

    public static class RandomS<A> extends StateM<Seed, A> {

        public static StateF<Seed, Integer> nextInt = RandomExamples::nextInt;

        private RandomS(StateF<Seed, A> run) {
            super(run);
        }

        @Override public <B> RandomS<B> map(Function<A, B> f) {
            return new RandomS<>(this.run.map(f));
        }

        public static RandomS<Integer> intRnd = new RandomS<>(nextInt);

        // create a random Boolean state monad, by mapping the original random int function
        public static RandomS<Boolean> boolRnd = new RandomS<>(nextInt.map(i -> i % 2 == 0));

        // another way to create a random boolean state monad
        public static RandomS<Boolean> boolRnd2 = intRnd.map(i -> i % 2 == 0);
    }

    public static class Generator3 {
        // Fully abstracted into StateM<S, A>
        public static RandomS<Integer> nextInt = RandomS.intRnd;

        public static RandomS<Boolean> nextBool = RandomS.boolRnd;

    }

}
