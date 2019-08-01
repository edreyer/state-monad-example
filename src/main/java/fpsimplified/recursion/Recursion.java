package fpsimplified.recursion;

import java.math.BigInteger;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.vavr.Function1;

import static fpsimplified.recursion.TailCall.ret;
import static fpsimplified.recursion.TailCall.sus;
import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

public class Recursion {

    // CoRecursion -- Passing the output from one iteration to the next.
    // Recursion -- Start at end, stop at base condition.  Delays computation until base found.

    // -------------------------------------------------------------------------------------------------
    // Addition
    // -------------------------------------------------------------------------------------------------

    // Recursive add function - not stack safe
    static int add(int x, int y) {
        return y == 0
            ? x
            : add(++x, --y);
    }

    static int safeAdd(int x, int y) {
        return add2(x, y).eval();
    }

    // With TailCall optimization (simulated)
    static TailCall<Integer> add2(int x, int y) {
        return y == 0
            ? ret(x)
            : sus(() -> add2(x + 1, y - 1));
    }

    private static void addDemo() {
        //System.out.println(add(1, 100_000)); // stack overflow
        System.out.println(safeAdd(1, 100_000));
    }

    // -------------------------------------------------------------------------------------------------
    // Sum
    // -------------------------------------------------------------------------------------------------

    // Sum a list, not eligible for Tail Call Elimination (TCE)
    static int sum(List<Integer> xs) {
        return xs.isEmpty()
            ? 0
            : head(xs) + sum(tail(xs));
    }

    // -------------------------------------------------------------------------------------------------

    // Sum2 rebuilt using helper method, setting it for for TCE
    // Stil not stack safe
    static Integer sum2(List<Integer> xs) {
        return sumTail(xs, 0);
    }

    // Helper method for sum2()
    private static Integer sumTail(List<Integer> xs, int acc) {
        return xs.isEmpty()
            ? acc
            : sumTail(tail(xs), acc + head(xs));
    }

    // -------------------------------------------------------------------------------------------------

    // Sum2 rebuilt using helper method, setting it for for TCE
    // Stil not stack safe
    static Integer sum3(List<Integer> xs) {
        return sumTailSafe(xs, 0).eval();
    }

    private static TailCall<Integer> sumTailSafe(List<Integer> xs, int acc) {
        return xs.isEmpty()
            ? ret(acc)
            : sus(() -> sumTailSafe(tail(xs), acc + head(xs)));
    }

    private static void sumDemo() {
        List<Integer> xs = IntStream.range(0, 50_000).boxed().collect(Collectors.toList());

        //System.out.println(sum(xs)); // stack overflow
        //System.out.println(sum2(xs)); // still stack overflow
        System.out.println(sum3(xs));
    }


    // -------------------------------------------------------------------------------------------------
    // Fibonacci
    // -------------------------------------------------------------------------------------------------

    // naive non-stack safe fibonacci
    static BigInteger fibonacci(int n) {
        if (n == 0 || n == 1) {
            return BigInteger.valueOf(n);
        }
        return fibonacci(n - 1).add(fibonacci( n - 2));
    }

    static BigInteger memoFib(int n) {
        if (n == 0 || n == 1) {
            return BigInteger.valueOf(n);
        }
        return memoizedFib.apply(n - 1).add(memoizedFib.apply( n - 2));
    }

    // Required or will take an eternity (quite literally)
    static Function1<Integer, BigInteger> memoizedFib = Function1.of(Recursion::memoFib).memoized();

    // -------------------------------------------------------------------------------------------------

    // refactored to use helper to prep for TCE
    // still NOT stack safe, but it is fast
    static BigInteger fibonacci2(int n) {
        return tailFib2(BigInteger.valueOf(n), ONE, ZERO);
    }

    private static BigInteger tailFib2(BigInteger n, BigInteger acc1, BigInteger acc2) {
        if (n.equals(ZERO)) {
            return ZERO;
        } else if (n.equals(ONE)) {
            return acc1.add(acc2);
        } else {
            return tailFib2(n.subtract(ONE), acc2, acc1.add(acc2));
        }
    }

    // -------------------------------------------------------------------------------------------------

    // FAST and STACK SAFE version
    static BigInteger fibonacci3(int n) {
        return tailFib3(BigInteger.valueOf(n), ONE, ZERO).eval();
    }

//    static TailCall<BigInteger> tailFib3(BigInteger n, BigInteger acc1, BigInteger acc2) {
//        if (n.equals(ZERO)) {
//            return ret(acc1.add(acc2));
//        } else if (n.equals(ONE)) {
//            return
//        }
//    }

    static TailCall<BigInteger> tailFib3(BigInteger n, BigInteger acc1, BigInteger acc2) {
        if (n.equals(ZERO)) {
            return ret(ZERO);
        } else if (n.equals(ONE)) {
            return ret(acc1.add(acc2));
        } else {
            return sus(() -> tailFib3(n.subtract(ONE), acc2, acc1.add(acc2)));
        }
    }

    private static void fibDemo() {
        var n = 10_000;

        //System.out.print("fib("+n+")=");
        //System.out.println(fibonacci(n)); // slooooooooooooooooooooooooow

        //System.out.print("memoized fib("+n+")=");
        //System.out.println(memoizedFib.apply(n)); // FAST, NOT stack safe

        //System.out.println(fibonacci2(n)); // stack overflow

        System.out.println("fib("+n+")=" + fibonacci3(n)); // FAST - no memoization

         //FYI: without memoization, fib(10000) will take 2^10000 * 10 nanoseconds.
         //Which means it will NEVER end
//        BigInteger years = BigInteger.valueOf(2).pow(10000)
//            .multiply(BigInteger.valueOf(10)) // nanoseconds
//            .divide(BigInteger.valueOf(10).pow(9)) // seconds
//            .divide(BigInteger.valueOf(60)) // minutes
//            .divide(BigInteger.valueOf(60)) // hours
//            .divide(BigInteger.valueOf(24)) // days
//            .divide(BigInteger.valueOf(365)); // years
//        System.out.println("Years to compute fib(10000) = " + years);
    }

    // -------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------


    public static void main(String[] args) {

        //addDemo();

        //sumDemo();

        fibDemo();

    }

    static <T> T head(List<T> ts) {
        return ts.isEmpty()
            ? null
            : ts.get(0);
    }

    static <T> List<T> tail(List<T> ts) {
        return ts.isEmpty()
            ? ts
            : ts.subList(1, ts.size());
    }


}
