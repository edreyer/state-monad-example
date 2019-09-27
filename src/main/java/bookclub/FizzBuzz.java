package bookclub;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn3;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;

public class FizzBuzz {

    @FunctionalInterface
    interface FizzTestA extends Fn3<Integer, String, Integer, String> {}

    static void a() {

        FizzTestA test = (divisor, label, num) -> (num % divisor == 0)
            ? label
            : "" + num;

        Fn1<Integer, String> fizz = test.apply(3, "Fizz");
        Fn1<Integer, String> buzz = test.apply(5, "Buzz");

        //fizz.fmap(buzz.apply(5)); // doesn't compile

        // Problem 1:   These don't compose. Output of String doesn't line up with Input of Integer
        // Problem 2:   Even if composed, You don't end up with combined effect
    }

    @FunctionalInterface
    interface FizzTestB extends Fn3<Integer, String, Tuple2<Integer, String>, Tuple2<Integer, String>> {}

    static void b() {
        // To get them to compose use Tuple2 to pass back out the input number
        FizzTestB test = (divisor, label, input) -> {
            Tuple2<Integer, String> foo = (input._1() % divisor == 0) ?
                tuple(input._1(), input._2() + label) :
                input;

            return foo;
        };

        Fn1<Tuple2<Integer, String>, Tuple2<Integer, String>> fizz =
            test.apply(3, "Fizz");
        Fn1<Tuple2<Integer, String>, Tuple2<Integer, String>> buzz =
            test.apply(5, "Buzz");

        Fn1<Tuple2<Integer, String>, Tuple2<Integer, String>> fizzBuzzTuple =
            fizz.fmap(foo -> buzz.apply(foo));

        Fn1<Integer, String> fizzBuzz = (Integer n) -> fizzBuzzTuple.apply(
            tuple(n, ""))._2();

        System.out.println(fizzBuzz.apply(2));
        System.out.println(fizzBuzz.apply(3));
        System.out.println(fizzBuzz.apply(5));
        System.out.println(fizzBuzz.apply(15));

        // Problem 1:   These compose but produce incorrect output:
        /*
            2
            3
            5Buzz
            FizzBuzz
         */
    }

    @FunctionalInterface
    interface FizzTestC extends Fn3<Integer, String, Fn1<String, String>, Fn1<String, String>> {}

    public static String c(Integer num) {

        // To get them to compose use Tuple2 to pass back out the input number
        FizzTestC test = (divisor, label, labelFn) -> {
            Fn1<String, String> labelCombiner = (__) -> (labelFn.apply("") + label);
            return (num % divisor == 0)
                ? labelCombiner
                : labelFn;
        };

        Fn1<Fn1<String, String>, Fn1<String, String>> fizz = test.apply(3, "Fizz");
        Fn1<Fn1<String, String>, Fn1<String, String>> buzz = test.apply(5, "Buzz");

        Fn1<Fn1<String, String>, Fn1<String, String>> fizzBuzzInternal =
            fizz.fmap(fn -> buzz.apply(fn));

        Fn1<String, String> fizzBuzz = fizzBuzzInternal.apply(n -> n);
        return fizzBuzz.apply("" + num);
    }

    public static void main(String[] args) {
        System.out.println(c(2));
        System.out.println(c(3));
        System.out.println(c(5));
        System.out.println(c(8));
        System.out.println(c(15));
    }


}
