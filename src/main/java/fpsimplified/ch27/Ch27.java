package fpsimplified.ch27;

import io.vavr.Function2;

import static fpsimplified.ch27.Ch27Solutions.*;
import static java.lang.System.out;

public class Ch27 {

    public static int sum(int a, int b) {
        return a + b;
    }

    public static void demoCurry() {

        var curried = curryFunction(Ch27::sum);

        var partial = curried.apply(5);

        var result = partial.apply(10);

        out.println("Sum(5)(10) = " + result);

    }

// Expand for live coding exercise

    //    public static ??? curried(BiFunction<T, U, R> func) {
    //        ???
    //    }


    public static void demoVavrCurry() {

        Function2<Integer, Integer, Integer> sum = (a, b) -> a + b;

        var curried = sum.curried();

        var partial = curried.apply(5);

        var result = partial.apply(10);

        out.println("Sum(5)(10) = " + result);

    }

    public static String wrap(String pre, String content, String post) {
        return pre + content + post;
    }

    public static void demoPartial() {

        var wrapWithDiv = wrapWithDiv(Ch27::wrap);

        out.println(wrapWithDiv.apply("<img src=\"foo.jpg\">"));
    }

// Expand for live coding exercise

    //    public static ??? myWrapWithDiv(Function3<String, String, String, String> func) {
//        ???
//    }

    public static void main(String[] args) {
        demoCurry();

        demoVavrCurry();

        demoPartial();
    }

}
