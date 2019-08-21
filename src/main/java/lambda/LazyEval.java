package lambda;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.jnape.palatable.lambda.functions.builtin.fn2.Iterate.iterate;
import static com.jnape.palatable.lambda.functions.builtin.fn2.LT.lt;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Take.take;
import static com.jnape.palatable.lambda.functions.builtin.fn2.TakeWhile.takeWhile;
import static com.jnape.palatable.lambda.functions.builtin.fn2.ToCollection.toCollection;

public class LazyEval {

    // 1) Traditional way
    // 2) If you ask for huge date span, this can be very very large
    public static List<LocalDate> daysBetween(LocalDate start, LocalDate end) {
        List<LocalDate> dates   = new ArrayList<>();
        LocalDate       current = start;
        while (!end.isBefore(current)) {
            dates.add(current);
            current = current.plusDays(1);
        }
        return dates;
    }

    public void hugeDateRangeExample() {
        LocalDate today             = LocalDate.now();
        LocalDate distantFuture     = LocalDate.of(3000, 12, 25);
        List<LocalDate> lotsOfDays  = daysBetween(today, distantFuture);

        // ... later

        List<LocalDate> whatWeWant = lotsOfDays.subList(0, 10); // chucked most of the computed days
    }

    // Lazy.  Nothing is materialized until we actuall call terminating function
    public static Iterable<LocalDate> daysBetween2(LocalDate start, LocalDate end) {
        return takeWhile(lt(end), iterate(current -> current.plusDays(1), start));
    }

    public void yieldDesiredValues() {
        LocalDate today             = LocalDate.now();
        LocalDate distantFuture     = LocalDate.of(3000, 12, 25);
        Iterable<LocalDate> lotsOfDays  = daysBetween2(today, distantFuture); // nothing happens yet

        // ... later

        Iterable<LocalDate> whatWeWant  = take(10, lotsOfDays); // ONLY 10 dates created
        List<LocalDate>     onHeap      = toCollection(ArrayList::new, whatWeWant); // memoize

    }
}
