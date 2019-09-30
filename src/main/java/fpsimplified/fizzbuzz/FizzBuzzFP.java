package fpsimplified.fizzbuzz;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.choice.Choice2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn3;
import com.jnape.palatable.lambda.functions.Fn4;

import static com.jnape.palatable.lambda.functions.builtin.fn2.Iterate.iterate;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Map.map;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Take.take;

public class FizzBuzzFP {

    static abstract class FizzBuzz<T> {
        protected final Choice2<Integer, String> choice;
        protected FizzBuzz(Choice2<Integer, String> choice) {
            this.choice = choice;
        }
        abstract public T getVal();

        @Override
        public String toString() {
            return getVal().toString();
        }
    }
    static class Carbonated extends FizzBuzz<String> {
        public Carbonated(String val) {
            super(Choice2.b(val));
        }
        public String getVal() {
            return choice.match(str -> "", str -> str);
        }
    }
    static class Uncarbonated extends FizzBuzz<Integer> {
        public Uncarbonated(Integer val) {
            super(Choice2.a(val));
        }
        public Integer getVal() {
            return choice.match(n -> n, n -> 0);
        }
    }

    static Fn3<Integer, String, Integer, FizzBuzz> carbonateFn =
        (Integer divisor, String label, Integer n) ->
            (n % divisor == 0)
                ? new Carbonated(label)
                : new Uncarbonated(n);

    static Fn4<
        Fn3<Integer, String, Integer, FizzBuzz>,
        Integer,
        String,
        FizzBuzz,
        FizzBuzz> ifUncarbonatedDoFn =
        (fn, divisor, label, fbv) -> switch (fbv.getClass().getSimpleName()) {
            case "Carbonated" -> fbv;
            case "Uncarbonated" -> fn.apply(divisor, label, (Integer)fbv.getVal());
            default -> throw new IllegalStateException();
        };

    static Fn1<Integer, String> fizzBuzzFn = (n) ->
        Maybe.just(n)
            .fmap(num -> carbonateFn.apply(15, "FizzBuzz", num))
            .fmap(fbv -> ifUncarbonatedDoFn.apply(carbonateFn, 3, "Fizz", fbv))
            .fmap(fbv -> ifUncarbonatedDoFn.apply(carbonateFn, 5, "Buzz", fbv))
            .fmap(fbv -> fbv.toString())
            .orElseThrow(() -> new RuntimeException());

    public static void main(String[] args) {
        Iterable<String> fizzBuzz100 = map(fizzBuzzFn,
            take(100,
                iterate(n -> n + 1, 1))
        );

        fizzBuzz100.forEach(System.out::println);
    }
}
