package fpsimplified.ch27;

import io.vavr.Function3;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Ch27Solutions {

    public static <T, U, R> Function<T, Function<U, R>> curryFunction(
        BiFunction<T, U, R> bifunc
    ) {
        return t -> u -> bifunc.apply(t, u);
    }

    public static Function<String, String> wrapWithDiv(
        Function3<String, String, String, String> wrapFunc
    ) {

        Function3<String, String, String, String> wrapF3 = wrapFunc;

        var wrapCurried = wrapF3.curried();

        return (content) -> wrapCurried
            .apply("<div>")
            .apply(content)
            .apply("</div>");

    }

}
