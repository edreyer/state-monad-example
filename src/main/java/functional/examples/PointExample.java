package functional.examples;

import functional.monad.StateM;
import functional.examples.RandomExamples.RandomS;
import functional.examples.RandomExamples.Seed;
import lombok.Value;

import static functional.monad.StateAPI.For;

public class PointExample {

    @Value
    public static class Point {
        public final int x, y, z;
        @Override public String toString() {
            return String.format("Point(%s, %s, %s)", x, y, z);
        }
    }

    public static void main(String[] args) {

        // create a new random point using random generator
        StateM<Seed, Point> program1 =
            RandomS.intRnd.flatMap(x ->
                RandomS.intRnd.flatMap(y ->
                    RandomS.intRnd.map(z ->
                        new Point(x, y, z)
            )));

        StateM<Seed, Point> program2 = For(
            RandomS.intRnd,
            RandomS.intRnd,
            RandomS.intRnd
        ).yield( (x, y, z) -> new Point(x, y, z) );

        Seed seed = new Seed(0);

        Point p1a = program1.eval(seed);
        Point p1b = program2.eval(seed);

        System.out.println("Next two points should be equal");
        System.out.println(p1a);
        System.out.println(p1b);

        Point p2a = program1.eval(null);
        Point p2b = program2.eval(null);

        System.out.println("\nNext two points should be different");
        System.out.println(p2a);
        System.out.println(p2b);


    }
}
