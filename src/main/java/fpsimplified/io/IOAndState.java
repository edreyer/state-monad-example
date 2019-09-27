package fpsimplified.IO;

import java.util.Scanner;

import com.jnape.palatable.lambda.adt.Unit;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.io.IO;
import com.jnape.palatable.lambda.monad.transformer.builtin.StateT;
import lombok.Value;
import lombok.experimental.Wither;

import static com.jnape.palatable.lambda.adt.Unit.UNIT;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.io.IO.io;

public class IOAndState {

    // -------------------------------------------------
    // Initial pass at methods

    static IO<String> getLine() {
        Scanner scanner = new Scanner(System.in);
        return io(() -> scanner.nextLine());
    }

    static IO<Unit> putStr(String s) {
        return io(() -> System.out.println(s));
    }

    // -------------------------------------------------
    // Updated methods normalized on StateT API

    // wraps an IO<A> in a StateT
    static <A> StateT<SumState, IO<?>, A> liftIoIntoState(IO<A> io) {
        return StateT.stateT(
            (SumState s) -> io.fmap(a -> tuple(a, s))
        );
    }

    static StateT<SumState, IO<?>, String> getLineAsStatetT() {
        return liftIoIntoState(getLine());
    }

    static StateT<SumState, IO<?>, Unit> putStrAsStateT(String s) {
        return liftIoIntoState(putStr(s));
    }

    // Helper method string -> int
    static int toInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Value  // lombok
    @Wither // lombok
    static class SumState {
        Integer sum;
    }

    // Given an Integer, return a StateT representing the operation to update
    // the State with the `newValue`
    static StateT<SumState, IO<?>, Integer> updateAppState(Integer newValue) {

        Fn1<SumState, IO<Tuple2<Integer, SumState>>> sumStateIOFn1 = (SumState oldState) -> {
            Integer newSum = newValue + oldState.getSum();

            System.out.println("updateIntState, old sum: " + oldState.sum);
            System.out.println("updateIntState, new input: " + newValue);
            System.out.println("updateIntState, new sum: " + newSum);

            SumState newState = oldState.withSum(newSum);
            return io(() -> tuple(newSum, newState));
        };

        StateT<SumState, IO<?>, Integer> sumStateFn1IOStateT =
            StateT.<SumState, IO<?>, Integer>stateT(sumStateIOFn1);

        return sumStateFn1IOStateT;
    }

    // UH OH!  Incompatible Typles.  Need a Monad Transformer (StateT) to help
    // This breaks because of incompatible APIS -- methods given IO when expecting StateT
//    static StateT<SumState, IO<?>, Unit> sumLoop() {
//        return putStr("\nGive me an int: ").flatMap(_1 ->
//        getLine().flatMap(input ->
//        io(() -> toInt(input))).flatMap(i ->
//        updateAppState(i)).flatMap(_2 ->
//        sumLoop()));
//    }

    static StateT<SumState, IO<?>, Unit> sumLoopUsingStateT() {
        return putStrAsStateT("\nGive me an int: ").flatMap(_1 ->
            getLineAsStatetT().flatMap(input ->
                (input.equals("q"))
                ? liftIoIntoState(IO.io(UNIT))
                : liftIoIntoState(io(() -> toInt(input))).flatMap(i ->
                    updateAppState(i).flatMap(_2 ->
                    sumLoopUsingStateT()))));
    }

    public static void main(String[] args) {
        IO io = IOAndState.sumLoopUsingStateT().runStateT(new SumState(0));
        io.unsafePerformIO();
    }

}
