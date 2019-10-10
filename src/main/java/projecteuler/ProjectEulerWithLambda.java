package projecteuler;

import com.jnape.palatable.lambda.functions.Fn0;
import com.jnape.palatable.lambda.functions.Fn2;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Head.head;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Filter.filter;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Iterate.iterate;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Map.map;
import static com.jnape.palatable.lambda.functions.builtin.fn2.ReduceLeft.reduceLeft;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Take.take;
import static com.jnape.palatable.lambda.functions.builtin.fn2.TakeWhile.takeWhile;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Unfoldr.unfoldr;

public class ProjectEulerWithLambda {

    // https://projecteuler.net/problem=1
    public static Integer problem1() {

        return
            reduceLeft(Integer::sum,
            filter(i -> i % 3 == 0 || i % 5 == 0,
            take(1000,
            iterate(a -> a + 1, 0)
        ))).orElseThrow(() -> new IllegalStateException());
    }

    // https://projecteuler.net/problem=2
    public static Integer problem2() {
        return
            reduceLeft(Integer::sum,
            filter(i -> i % 2 == 0,
            takeWhile(i -> i < 4_000_000,
            unfoldr(state ->
                    just(tuple(state._1(), tuple(state._2(), state._1() + state._2()))),
                    tuple(1, 2))
            ))).orElseThrow(() -> new IllegalStateException());
    }

    // https://projecteuler.net/problem=3

    public static boolean isPrime(long number) {
        if (number == 2 || number == 3) {
            return true;
        }
        if (number % 2 == 0) {
            return false;
        }
        int sqrt = (int) Math.sqrt(number) + 1;
        for (int i = 3; i < sqrt; i += 2) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static Long problem3() {
        final long target = 600851475143L;

        return
        head(
        filter(i -> isPrime(i),
        filter(i -> target % i == 0,
        filter(i -> i % 2 != 0,
        iterate(a -> a - 1, 1 + (target / 2)) )))).
        orElseThrow(() -> new IllegalStateException());
    }


    public static void main(String[] args) {
        printResult(1, ProjectEulerWithLambda::problem1);
        printResult(2, ProjectEulerWithLambda::problem2);
        printResult(3, ProjectEulerWithLambda::problem3);
    }

    private static void printResult(int problemNum, Fn0<?> fn) {
        System.out.println("Problem " + problemNum + ": " + fn.apply().toString());
    }
}
