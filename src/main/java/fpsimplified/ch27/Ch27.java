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

        var wrapWithDiv = Ch27Solutions.wrapWithDiv(Ch27::wrap);

        out.println(wrapWithDiv.apply("<img src=\"foo.jpg\">"));
    }

    public static void main(String[] args) {
        demoCurry();

        demoVavrCurry();

        demoPartial();
    }

}
