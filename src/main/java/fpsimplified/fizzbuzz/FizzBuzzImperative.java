package fpsimplified.fizzbuzz;

import static java.lang.System.out;

public class FizzBuzzImperative {

    /**
     * Not Composable.  It's a mini-monolith
     * Also, not pure
     * Also, mixing in I/O
     */
    public static void fizzBuzz() {
        for (int i = 1; i <= 100 ; i++) {
            if (i % 15 == 0) {
                out.println("FizzBuzz");
            } else if (i % 3 == 0) {
                out.println("Fizz");
            } else if (i % 5 == 0) {
                out.println("Buzz");
            } else {
                out.println(i);
            }
        }
    }

    public static void main(String[] args) {
        fizzBuzz();
    }
}
