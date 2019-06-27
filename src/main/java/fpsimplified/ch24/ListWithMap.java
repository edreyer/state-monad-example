package fpsimplified.ch24;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ListWithMap<A> extends LinkedList<A> {

    public static <A> ListWithMap<A> of(A a) {
        ListWithMap<A> result = new ListWithMap<>();
        result.add(a);
        return result;
    }

    public static <A> ListWithMap<A> of(A... as) {
        ListWithMap<A> result = new ListWithMap<>();
        for (A a : as) {
            result.add(a);
        }
        return result;
    }

    public <B> ListWithMap<B> map(Function<? super A, ? extends B> f) {
        ListWithMap<B> result = new ListWithMap<>();
        for (A a : this) {
            result.add(f.apply(a));
        }
        return result;
    }

    public ListWithMap<A> filter(Predicate<? super A> p) {
        ListWithMap<A> result = new ListWithMap<>();
        for (A a : this) {
            if (p.test(a)) {
                result.add(a);
            }
        }
        return result;
    }

    public <B> ListWithMap<B> flatMap(Function<? super A, ? extends ListWithMap<? extends B>> f) {
        ListWithMap<B> result = new ListWithMap<>();
        for (A a : this) {
            for (B b : f.apply(a)) {
                result.add(b);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        ListWithMap<String> input = ListWithMap.of("1", "2", "3", "4", "5", "6");

        List<Integer> output = input
            .map(Integer::parseInt)
            .filter(n -> n % 2 == 0)
            .flatMap(n -> {
                ListWithMap<Integer> xs = new ListWithMap<>();
                xs.add(n * 2);
                xs.add(n * -1);
                return xs;
            });

        output.forEach(o -> System.out.println(o + " "));

    }

}
