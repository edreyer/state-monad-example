package functional.examples;

import functional.examples.RandomExamples.Generator1;
import functional.examples.RandomExamples.Generator2;
import functional.examples.RandomExamples.Generator3;
import functional.examples.RandomExamples.RandomS;
import functional.examples.RandomExamples.Seed;
import functional.monad.StateTuple;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RandomExamplesTest {

    @Test
    public void testGenerator1() {
        Seed seed = new Seed(0);

        StateTuple<Integer, Seed> t1 = Generator1.nextInt(seed);
        StateTuple<Integer, Seed> t2 = Generator1.nextInt(t1.state);
        StateTuple<Integer, Seed> t3 = Generator1.nextInt(t2.state);

        assertEquals(Integer.valueOf(-1155484576), t1.value);
        assertEquals(Integer.valueOf(-1764305998), t2.value);
        assertEquals(Integer.valueOf(-131000125), t3.value);

        // same input, same output
        assertEquals(Integer.valueOf(-131000125), Generator1.nextInt(t2.state).value);
        assertEquals(Integer.valueOf(-131000125), Generator1.nextInt(t2.state).value);
        assertEquals(Integer.valueOf(-131000125), Generator1.nextInt(t2.state).value);

    }

    @Test
    public void testGenerator2() {
        Seed seed = new Seed(0);

        StateTuple<Integer, Seed> t1 = Generator2.nextInt.apply(seed);
        StateTuple<Integer, Seed> t2 = Generator2.nextInt.apply(t1.state);
        StateTuple<Integer, Seed> t3 = Generator2.nextInt.apply(t2.state);

        assertEquals(Integer.valueOf(-1155484576), t1.value);
        assertEquals(Integer.valueOf(-1764305998), t2.value);
        assertEquals(Integer.valueOf(-131000125), t3.value);

        // same input, same output
        assertEquals(Integer.valueOf(-131000125), Generator2.nextInt.apply(t2.state).value);
        assertEquals(Integer.valueOf(-131000125), Generator2.nextInt.apply(t2.state).value);
        assertEquals(Integer.valueOf(-131000125), Generator2.nextInt.apply(t2.state).value);
    }

    @Test
    public void testGenerator3() {
        Seed seed = new Seed(0);

        // get next tuple
        StateTuple<Integer, Seed> t1 = Generator3.nextInt.run.apply(seed);
        StateTuple<Integer, Seed> t2 = Generator3.nextInt.run.apply(t1.state);
        StateTuple<Integer, Seed> t3 = Generator3.nextInt.run.apply(t2.state);

        assertEquals(Integer.valueOf(-1155484576), t1.value);
        assertEquals(Integer.valueOf(-1764305998), t2.value);
        assertEquals(Integer.valueOf(-131000125), t3.value);

        // same input, same output
        assertEquals(Integer.valueOf(-131000125), Generator3.nextInt.eval(t2.state));
        assertEquals(Integer.valueOf(-131000125), Generator3.nextInt.eval(t2.state));
        assertEquals(Integer.valueOf(-131000125), Generator3.nextInt.eval(t2.state));

        assertEquals(Boolean.FALSE, Generator3.nextBoolean.eval(t2.state));

    }

    @Test
    public void testRandomSDirectUse() {
        Seed seed = new Seed(0);

        // get next tuple
        StateTuple<Integer, Seed> r1 = RandomS.intRnd.run.apply(seed);

        // with helper
        StateTuple<Integer, Seed> r2 = RandomS.intRnd.apply(r1.state);

        // Just want the random, forget the updated state
        Integer rand = RandomS.intRnd.eval(r2.state);

        //Boolean bool = RandomS.nextBool.eval(r2._2);

    }

}
