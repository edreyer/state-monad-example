package fpsimplified.ch40;

import java.util.Random;
import java.util.Scanner;

import io.vavr.control.Try;
import lombok.Value;
import lombok.experimental.Wither;

import static fpsimplified.ch40.CoinFlipUtils.getUserInput;
import static fpsimplified.ch40.CoinFlipUtils.printGameOver;
import static fpsimplified.ch40.CoinFlipUtils.printState;
import static fpsimplified.ch40.CoinFlipUtils.printableFlipResult;
import static fpsimplified.ch40.CoinFlipUtils.showPrompt;
import static fpsimplified.ch40.CoinFlipUtils.tossCoin;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.run;
import static io.vavr.Predicates.isIn;

public class CoinFlip {

    public static void main(String[] args) {
        State s = new State(0, 0);
        Random r = new Random();
        mainLoop(s, r);
    }


    public String foo() {
        throw new UnsupportedOperationException();
    }

    public Try<String> bar() {
        return Try.success("bar");
    }

    public void caller() {
        try {
            bar();
        } catch (Exception e) {
            // ...
        }

        String bar = bar()
            .onFailure(e -> System.out.println(e.getMessage()))
            .getOrElse("oopsy");
    }


    public static void mainLoop(State state, Random rand) {
        showPrompt();
        final String userInput = getUserInput();

        Match(userInput).of(
            Case($(isIn("H", "T")), ui -> run(() -> {
                State newState = handleCoinToss(ui, state, rand);
                mainLoop(newState, rand);
            })),

            Case($(), it -> run(() -> handleQuit(state)))
        );


    }

    public static State handleCoinToss(String userInput, State state, Random rand) {
        String toinCossResult = tossCoin(rand);
        State newState = (userInput.equals(toinCossResult)
            ? state.withNumCorrect(state.getNumCorrect() + 1)
            : state)
            .withNumFlips(state.getNumFlips() + 1);
        printState(printableFlipResult(toinCossResult), newState);
        return newState;
    }

    public static void handleQuit(State state) {
            printGameOver();
            printState(state);
    }

}

@Value
@Wither
class State {
    private int numFlips;
    private int numCorrect;
}

class CoinFlipUtils {

    private static Scanner sc = new Scanner(System.in);

    public static void showPrompt() {
        System.out.print("\n(h)eads, (t)ails, (q)uit: ");
    }

    public static String getUserInput() {
        return sc.nextLine().trim().toUpperCase();
    }

    public static String printableFlipResult(String flip) {
        return Match(flip).of(
            Case($("H"), "Heads"),
            Case($("T"), "Tails")
        );
    }

    public static void printState(String printableFlip, State state) {
        System.out.println("Flip was " + printableFlip);
        printState(state);
    }

    public static void printState(State state) {
        System.out.println(String.format(
            "#Flips: %d, #Correct: %d",
            state.getNumFlips(),
            state.getNumCorrect()
        ));
    }

    public static void printGameOver() {
        System.out.println("\n====== Game Over =====");
    }

    public static String tossCoin(Random r) {
        return Match(r.nextInt(2)).of(
            Case($(0), "H"),
            Case($(1), "T")
        );
    }
}