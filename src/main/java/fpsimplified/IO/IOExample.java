package fpsimplified.IO;

import com.jnape.palatable.lambda.adt.Unit;
import com.jnape.palatable.lambda.functions.Fn0;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.io.IO;

import java.util.Scanner;

import static com.jnape.palatable.lambda.io.IO.io;

public class IOExample {

    public static void main(String[] args) {

        final Scanner scanner = new Scanner(System.in);

        Fn0<IO<String>> getLine = () ->
            io(() -> scanner.nextLine());

        Fn1<String, IO<Unit>> putStrLn = (String s) ->
            io(() -> System.out.println(s));

        IO<Unit> ioWork = putStrLn.apply("First Name?")
            .flatMap( unit -> getLine.apply()
            .flatMap( first -> putStrLn.apply("Last Name?")
            .flatMap( unit2 -> getLine.apply()
            .flatMap( last -> putStrLn.apply("First: " + first + ", Last: " + last)))));

        ioWork.unsafePerformIO();
    }
}
