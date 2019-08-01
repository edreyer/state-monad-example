package bookclub;

import java.util.Random;

import io.vavr.Function1;
import lombok.Value;

public class StateExercise {

    @Value
    public static class Seed{
        private int seed;
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

        // function form: S -> (S', A)
        // Function1<Seed, StateTuple<Seed, Boolean>>
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


    public interface StateF<S, A> extends Function1<S, StateTuple<S, A>> {}

    public static class StateM<S, A> {

        private StateF<S, A> op;

        public static <S, A> StateM<S, A> of(StateF<S, A> op) {
            // TODO
            return null;
        }

        public static <S, A> StateM<S, A> of(A a) {
            // TODO
            return null;
        }

        public A eval(S s) {
            // given the state, return a value
            return null;
        }

        public <B> StateM<S, B> flatMap(Function1<A, StateM<S, B>> f) {
            // TODO
            return null;
        }

        public <B> StateM<S, B> map(Function1<A, B> f) {
            // TODO: define in terms of flatMap
            return null;
        }

    }

    public static void main(String[] args) {

        // Example usage
        StateM<Seed, Integer> rndInt = StateM.of(StateExercise::nextInt);

        // generate a random vaue
        System.out.println(
            rndInt.eval(new Seed(0))
        );

        @Value
        class Point {
            private int x, y, z;
        }


        StateM<Seed, Point> pointS = rndInt.flatMap(x ->
            rndInt.flatMap(y ->
                rndInt.map(z -> new Point(x, y, z))));

        Point point = pointS.eval(new Seed(0));

    }

}
