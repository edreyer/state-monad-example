package functional.examples;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.IntStream;

import functional.monad.StateM;

import static io.vavr.API.println;

class Memo extends HashMap<BigInteger, BigInteger> {

    public Optional<BigInteger> retrieve(BigInteger key) {
        return Optional.ofNullable(super.get(key));
    }

    public Memo addEntry(BigInteger key, BigInteger value) {
        super.put(key, value);
        return this;
    }
}

public class FibonacciUsingState {

    static BigInteger fibMemo2(BigInteger n) {
        return fibMemo(n).eval(new Memo().addEntry(BigInteger.ZERO, BigInteger.ZERO).addEntry(BigInteger.ONE, BigInteger.ONE));
    }

    static StateM<Memo, BigInteger> fibMemo(BigInteger n) {
        return StateM.getState((Memo m) -> m.retrieve(n))
            .flatMap(u -> u.map(StateM::<Memo, BigInteger>unit).orElse(fibMemo(n.subtract(BigInteger.ONE))
                .flatMap(x -> fibMemo(n.subtract(BigInteger.ONE).subtract(BigInteger.ONE))
                    .map(x::add)
                    .flatMap(z -> StateM.transition((Memo m) -> m.addEntry(n, z), z)))));
    }

    public static void main(String[] args) {
        IntStream.range(0, 500).forEach(i ->
            println(fibMemo2(BigInteger.valueOf(i)))
        );
    }
}
