package lambda;

import com.jnape.palatable.lambda.functions.Fn0;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.monoid.Monoid;
import com.jnape.palatable.lambda.semigroup.Semigroup;

import static com.jnape.palatable.lambda.functions.builtin.fn2.DropWhile.dropWhile;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Filter.filter;
import static com.jnape.palatable.lambda.functions.builtin.fn2.InGroupsOf.inGroupsOf;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Iterate.iterate;
import static com.jnape.palatable.lambda.functions.builtin.fn2.LTE.lte;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Map.map;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Take.take;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static com.jnape.palatable.lambda.monoid.Monoid.monoid;
import static java.util.Arrays.asList;

public class LambdaExamples {

    public static void curryingAndPartialApplication() {

        Fn2<Integer, Integer, Integer> add = Integer::sum;

        // Curried
        Fn1<Integer, Fn1<Integer, Integer>> alsoAdd = add;

        // Partial Application
        Fn1<Integer, Integer> add1 = add.apply(1);

        // Or full application
        Integer sum = add.apply(1, 2);

        // Deferred computation
        Fn0<Integer> deferredSum = add1.thunk(2);
    }

    public static void semiGroupAndMonoidExamples() {

        // Semigroup - binary associative operation that can't escape Integer...
        Semigroup<Integer> sumSg = Integer::sum;

        // ... which gives us "fold" operations
        Integer foldedSum = sumSg.foldLeft(0, asList(1, 2, 3)); // 6

        // Promoted to Monoid, allows us to fold without the starting value
        Monoid<Integer> sumM = monoid(sumSg, 0);

        // "reduce" is folding without starting value
        Integer reducedSum = sumM.reduceLeft(asList(1, 2, 3)); // 6

        // And this cool guy, which allows us to map and reduce in a single operation
        Integer sumLengths = sumM.foldMap(String::length, asList("foo", "bar", "baz"));
    }

    public static void miscellaneousApiExamples() {

        // NOTE:  All of this is lazily evaluated.  Nothing happens until we take

        Iterable<Integer> nats              = iterate(nat -> nat + 1, 0);

        Iterable<Integer> evens             = filter(x -> x % 2 == 0, nats);

        Iterable<Integer> evenSquares       = map(x -> x * x, evens);

        Iterable<Integer> firstHundred      = take(100, evenSquares);

        Iterable<Integer> greaterThan2500   = dropWhile(lte(2500), firstHundred);

        Iterable<Iterable<Integer>> groupedInTens   = inGroupsOf(10, greaterThan2500);

        Iterable<Integer> sum               = map(foldLeft(Integer::sum, 0), groupedInTens);

        sum.forEach(System.out::println); // No work happens until now
    }


}
