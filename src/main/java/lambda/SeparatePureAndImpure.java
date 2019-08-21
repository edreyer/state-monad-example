package lambda;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.jnape.palatable.lambda.functions.builtin.fn2.Map;
import com.jnape.palatable.lambda.io.IO;

import static com.jnape.palatable.lambda.functions.builtin.fn1.Distinct.distinct;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Flatten.flatten;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Map.map;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Sequence.sequence;
import static com.jnape.palatable.lambda.functions.builtin.fn2.SortWith.sortWith;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Take.take;
import static com.jnape.palatable.lambda.io.IO.io;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;

public class SeparatePureAndImpure {

    // mixed pure and impure
    public static Iterable<String> readTenLongestWords(Path path) throws IOException {
        return Map.<String, Iterable<String>>map(line -> asList(line.split("\\W+")))
            .fmap(flatten())
            .fmap(map(String::toLowerCase))
            .fmap(distinct())
            .fmap(sortWith(comparing(String::length).reversed()))
            .fmap(take(10))
            .apply(Files.readAllLines(path));
    }

    // side effect mixed with pure operations
    public void pureAndImpureMixed() throws IOException {
        // Nothing is handling the IOException, so we get a compile error
        Iterable<String> words = readTenLongestWords(Paths.get("/tmp/two_cities.txt"));
    }

    /////////////////////////////////
    // Separate pure and impure
    /////////////////////////////////

    public static Iterable<String> readTenLongestWordsPure(Iterable<String> lines) {
        return Map.<String, Iterable<String>>map(line -> asList(line.split("\\W+")))
            .fmap(flatten())
            .fmap(map(String::toLowerCase))
            .fmap(distinct())
            .fmap(sortWith(comparing(String::length).reversed()))
            .fmap(take(10))
            .apply(lines);
    }

    public static IO<List<String>> readLines(Path path) {
        return io(() -> Files.readAllLines(path));
    }

    public void canYouKeepEmSeparated() {
        // Nothing happens yet...
        IO<Iterable<String>> wordsIO = readLines(Paths.get("/tmp/two_cities.txt"))
            .fmap(SeparatePureAndImpure::readTenLongestWordsPure);

        // NOW all the work is performed
        Iterable<String> words = wordsIO.unsafePerformIO();
    }

    ///////////////
    // And it composes nicely
    ///////////////

    public static void demoComposition() {
        Iterable<Path> paths = asList(
            Paths.get("/tmp/two_cities.txt"),
            Paths.get("/tmp/sun_also_rises.txt")
        );

        Iterable<IO<List<String>>> readAllFiles = map(SeparatePureAndImpure::readLines, paths);

        IO<Iterable<String>> parallelizableIO = sequence(readAllFiles, IO::io)
            .fmap(flatten())
            .fmap(SeparatePureAndImpure::readTenLongestWordsPure);

        // Nothing has actually happened yet!!

        CompletableFuture<Iterable<String>> words = parallelizableIO.unsafePerformAsyncIO();
    }

}
