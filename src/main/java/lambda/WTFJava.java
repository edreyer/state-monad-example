package lambda;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

import com.jnape.palatable.lambda.adt.Either;
import com.jnape.palatable.lambda.adt.Maybe;

import static com.jnape.palatable.lambda.adt.Either.right;
import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Sequence.sequence;
import static java.util.Arrays.asList;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class WTFJava {

    public void inconsistentAPIs() {

        // Java 8 new types.
        // All of these provide a mechanism for providing a function
        // to transform it's contained data into something else
        Function<String, Integer>   function    = String::length;
        Optional<Integer>           optional    = Optional.of(1);
        Stream<Integer>             stream      = Stream.of(1, 2, 3);
        CompletableFuture<Integer>  future      = completedFuture(1);

        // Example of such a function that we can use to compose with the above to
        // transform the wrapped
        Function<Integer, Float>    plusOneToFloat = x -> x + 1F;

        // All of these do exactly the same thing.
        // No common interface.
        // Each type uses its own semantics
        Function<String, Float>     mappedFunction  = function.andThen(plusOneToFloat);
        Optional<Float>             mappedOptional  = optional.map(plusOneToFloat);
        Stream<Float>               mappedFoat      = stream.map(plusOneToFloat);
        CompletableFuture<Float>    mappedFuture    = future.thenApply(plusOneToFloat);

        // OK, so 'mapping' isn't all that powerful, but what about 'flat mapping'?

        // Function DOESN'T SUPPORT THIS AT ALL, but it's easy to implement, so WTF?

        Optional<Float> flatMappedOptional = optional
            .flatMap(x -> x % 2 == 0 ? Optional.of(x / 2F) : Optional.empty());

        Stream<Float>   flatMappedStream = stream
            .flatMap(x -> x % 2 == 0 ? Stream.of(x / 2F) : Stream.empty());

        // Exact same operation, completely different API
        CompletableFuture<Float> flatMappedFuture = future
            .thenCompose(x -> x % 2 == 0
                ? completedFuture(x / 2F)
                : new CompletableFuture<>() {{
                    completeExceptionally(new IllegalStateException("oops"));
                }});
    }

    public void consistentAPIs() {

        // These all work because 'lambda' types all use the same base interfaces
        // Maybe is a Monad, which is also an Applicative, which is also a Functor
        Maybe<Integer>  maybe       = just(1);
        Maybe<Float>    mappedMaybe = just(1)
            .flatMap(x -> x % 2 == 0 ? just(x / 2F) : nothing());

        // Allows some cool operations
        Iterable<Maybe<Integer>> maybes = asList(just(1), just(2), just(3));
        // Just [1, 2, 3]
        Maybe<Iterable<Integer>> flipped = sequence(maybes, Maybe::just);

        // Which also works with same APIs on Either
        Iterable<Either<String, Integer>> eithers = asList(right(1), right(2), right(3));
        // Right [1, 2, 3
        Either<String, Iterable<Integer>> alsoFlipped = sequence(eithers, Either::right);
    }
}
