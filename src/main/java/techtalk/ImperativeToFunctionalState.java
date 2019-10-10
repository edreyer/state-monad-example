package techtalk;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functor.builtin.State.state;

public class ImperativeToFunctionalState {

    static Double getPrice(String name) {
        switch (name) {
            case "carg": return 30.0;
            case "goog": return 1200.0;
        }
        return 10.0;
    }

    public static class Imperative {

        static Tuple2<Double, Map<String, Double>> buy(String name, Double amount, Map<String, Double> portfolio) {
            final var purchased = amount / getPrice(name);
            final var owned = portfolio.getOrElse(name, 0D);
            return tuple(purchased, portfolio.put(name, owned + purchased));
        }

        static Tuple2<Double, Map<String, Double>> sell(String name, Double quantity, Map<String, Double> portfolio) {
            final var revenue =  quantity * getPrice(name);
            final var owned = portfolio.getOrElse(name, 0D);
            return tuple(revenue, portfolio.put(name, owned - quantity));
        }

        static Double get(String name, Map<String, Double> portfolio) {
            return portfolio.getOrElse(name, 0D);
        }

        static Tuple2<Tuple2<Double, Double>, Map<String, Double>> move(String from, String to, Map<String, Double> portfolio) {
            final var originallyOwned = get(from, portfolio);
            Tuple2<Double, Map<String, Double>> sold = sell(from, originallyOwned, portfolio);

            final var revenue = sold._1();
            final var newPortfolio = sold._2();
            Tuple2<Double, Map<String, Double>> bought = buy(to, revenue, newPortfolio);

            final var purchased = bought._1();
            final var veryNewPortfolio = bought._2();
            return tuple(tuple(originallyOwned, purchased), veryNewPortfolio);
        }
    }

    public static class Functional {

        interface Transaction<A> extends Fn1<Map<String, Double>, Tuple2<A, Map<String, Double>>> {}

        static Fn2<String, Double, Transaction<Double>> buyFn = (name, amount) ->
            (Map<String, Double> portfolio) -> {
                final var purchased = amount / getPrice(name);
                final var owned = portfolio.getOrElse(name, 0D);
                return tuple(purchased, portfolio.put(name, owned + purchased));
            };

        static Fn2<String, Double, Transaction<Double>> sellFn = (name, quantity) ->
            (Map<String, Double> portfolio) -> {
                final var revenue =  quantity * getPrice(name);
                final var owned = portfolio.getOrElse(name, 0D);
                return tuple(revenue, portfolio.put(name, owned - quantity));
            };

        static Fn1<String, Transaction<Double>> getFn = (name) ->
            (Map<String, Double> portfolio) -> tuple(portfolio.getOrElse(name, 0D), portfolio);


        static Fn2<String, String, Transaction<Tuple2<Double, Double>>> moveFn = (from, to) ->
            portfolio -> state(getFn.apply(from))
                .flatMap(originallyOwned -> state(sellFn.apply(from, originallyOwned))
                .flatMap(revenue -> state(buyFn.apply(to, revenue))
                .fmap(purchased -> tuple(originallyOwned, purchased))))
                .run(portfolio);


        public static void main(String[] args) {
            Transaction<Tuple2<Double, Double>> moveGoogToCarg = moveFn.apply("goog", "carg");

            Map portfolio = HashMap.of("carg", 10.0, "goog", 100.0);
            Tuple2 result = moveGoogToCarg.apply(portfolio);

            System.out.println(result._1());
            System.out.println(result._2());

        }

    }

}
