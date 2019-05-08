package functional.examples;

import org.junit.Test;

import functional.examples.RandomExamples.Generator1;
import functional.examples.RandomExamples.Generator2;
import functional.examples.RandomExamples.Generator3;
import functional.examples.RandomExamples.RandomS;
import functional.examples.RandomExamples.Seed;
import io.vavr.Tuple2;

import static org.junit.Assert.assertEquals;

public class RandomExamplesTest {

    @Test
    public void testGenerator1() {
        Seed seed = new Seed(0);

        Tuple2<Integer, Seed> t1 = Generator1.nextInt(seed);
        Tuple2<Integer, Seed> t2 = Generator1.nextInt(t1._2());
        Tuple2<Integer, Seed> t3 = Generator1.nextInt(t2._2());

        assertEquals(Integer.valueOf(-1155484576), t1._1());
        assertEquals(Integer.valueOf(-1764305998), t2._1());
        assertEquals(Integer.valueOf(-131000125), t3._1());

        // same input, same output
        assertEquals(Integer.valueOf(-131000125), Generator1.nextInt(t2._2())._1());
        assertEquals(Integer.valueOf(-131000125), Generator1.nextInt(t2._2())._1());
        assertEquals(Integer.valueOf(-131000125), Generator1.nextInt(t2._2())._1());

    }

    @Test
    public void testGenerator2() {
        Seed seed = new Seed(0);

        Tuple2<Integer, Seed> t1 = Generator2.nextInt.apply(seed);
        Tuple2<Integer, Seed> t2 = Generator2.nextInt.apply(t1._2());
        Tuple2<Integer, Seed> t3 = Generator2.nextInt.apply(t2._2());

        assertEquals(Integer.valueOf(-1155484576), t1._1());
        assertEquals(Integer.valueOf(-1764305998), t2._1());
        assertEquals(Integer.valueOf(-131000125), t3._1());

        // same input, same output
        assertEquals(Integer.valueOf(-131000125), Generator2.nextInt.apply(t2._2())._1());
        assertEquals(Integer.valueOf(-131000125), Generator2.nextInt.apply(t2._2())._1());
        assertEquals(Integer.valueOf(-131000125), Generator2.nextInt.apply(t2._2())._1());
    }

    @Test
    public void testGenerator3() {
        Seed seed = new Seed(0);

        // get next tuple
        Tuple2<Integer, Seed> t1 = Generator3.nextInt.run.apply(seed);
        Tuple2<Integer, Seed> t2 = Generator3.nextInt.run.apply(t1._2());
        Tuple2<Integer, Seed> t3 = Generator3.nextInt.run.apply(t2._2());

        assertEquals(Integer.valueOf(-1155484576), t1._1());
        assertEquals(Integer.valueOf(-1764305998), t2._1());
        assertEquals(Integer.valueOf(-131000125), t3._1());

        // same input, same output
        assertEquals(Integer.valueOf(-131000125), Generator3.nextInt.eval(t2._2()));
        assertEquals(Integer.valueOf(-131000125), Generator3.nextInt.eval(t2._2()));
        assertEquals(Integer.valueOf(-131000125), Generator3.nextInt.eval(t2._2()));

        assertEquals(Boolean.FALSE, Generator3.nextBoolean.eval(t2._2));

    }

    @Test
    public void testRandomSDirectUse() {
        Seed seed = new Seed(0);

        // get next tuple
        Tuple2<Integer, Seed> r1 = RandomS.intRnd.run.apply(seed);

        // with helper
        Tuple2<Integer, Seed> r2 = RandomS.intRnd.apply(r1._2);

        // Just want the random, forget the updated state
        Integer rand = RandomS.intRnd.eval(r2._2);

        //Boolean bool = RandomS.nextBool.eval(r2._2);

    }

}
