package lambda;

import com.jnape.palatable.lambda.adt.Either;
import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.Try;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import lombok.Value;

public abstract class EitherExample {

    @Value
    public static class Payload {
        private String value;
    }

    /*
        Probem: 4 possible return values, but only two valid
     */
    abstract Tuple2<Maybe<Error>, Maybe<Payload>> parseInput(String input);

    /*
        Benefit: 4 possible outcomes reduced to only 2
     */
    abstract Either<Throwable, Payload> parseInput2(String input);

    /*
        Benefit: If negative outcome is Throwable, use Try instead of Either
     */
    abstract Try<Payload> parseInput3(String input);
}
