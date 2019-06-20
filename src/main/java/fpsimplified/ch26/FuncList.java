package fpsimplified.ch26;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FuncList<A> extends LinkedList<A> {

    public static <A> FuncList<A> of(A a) {
        FuncList<A> result = new FuncList<>();
        result.add(a);
        return result;
    }

    public static <A> FuncList<A> of(A... as) {
        FuncList<A> result = new FuncList<>();
        for (A a : as) {
            result.add(a);
        }
        return result;
    }

    public <B> FuncList<B> map(Function<? super A, ? extends B> a2b) {
        FuncList<B> result = new FuncList<>();
        for (A a : this) {
            result.add(a2b.apply(a));
        }
        return result;
    }

    public FuncList<A> filter(Predicate<? super A> p) {
        FuncList<A> result = new FuncList<>();
        for (A a : this) {
            if (p.test(a)) {
                result.add(a);
            }
        }
        return result;
    }

    public <B> FuncList<B> flatMap(Function<? super A, ? extends FuncList<? extends B>> a2bs) {
        FuncList<B> result = new FuncList<>();
        for (A a : this) {
            // because f.apply(a) returns a 'List' type, we need another 'for' loop
            for (B b : a2bs.apply(a)) {
                result.add(b);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        example1();
        example2();
        //example3();

        List<Integer> ints = IntStream.range(0, 100).boxed().collect(Collectors.toList());
    }

    public static void example1() {
        FuncList<String> input = FuncList.of("1", "2", "3", "4", "5", "6");

        List<Integer> output = input
            .map(Integer::parseInt)
            .filter(n -> n % 2 == 0)
            .flatMap(n -> {
                FuncList<Integer> xs = new FuncList<>();
                xs.add(n * 2);
                xs.add(n * n);
                return xs;
            });

        output.forEach(o -> System.out.println(o + " "));
    }

    public static void example2() {
        // mapping with function composition
        FuncList<Integer> prices = FuncList.of(10, 20, 30, 40, 50);

        Function<Integer, Double> addTax = p -> p + p * 0.05;
        Function<Double, Double> addShipping = p -> p + 9.95;

        // map twice
        FuncList<Double> result1 = prices
            .map(addTax)
            .map(addShipping);

        // two ways to create Higher Order Functions
        Function<Integer, Double> addTaxAndShipping = addTax.andThen(addShipping);
        Function<Integer, Double> addTaxAndShipping2 = addShipping.compose(addTax);

        // map once by combining functions with function composition
        FuncList<Double> result2 = prices.map(addTaxAndShipping);

        System.out.print("Result 1: ");
        result1.forEach(p -> System.out.print(p));

        System.out.println("");

        System.out.print("Result 2: ");
        result2.forEach(p -> System.out.print(p));

    }

    public static void example3() {

        // calling map multiple times.
        // Once for each transformation

//        Optional<String> url = Optional.ofNullable(listing).
//            map(InventoryListing::getMainPictureDefinition).
//            map(def -> pictureHelper.getScaledPicture(def, SIZE_152x114)).
//            map(ListingMetadataPicture::getUrl);
//
//        // Assign mapping operations to functions so we can compose them
//
//        Function<InventoryListing, ListingMetadataPictureDefinition> l2mpd =
//            InventoryListing::getMainPictureDefinition;
//
//        Function<ListingMetadataPictureDefinition, ListingMetadataPicture> mpd2lmp =
//            def -> pictureHelper.getScaledPicture(def, SIZE_152x114);
//
//        Function<ListingMetadataPicture, String> lmp2url = ListingMetadataPicture::getUrl;
//
//        // compose operations
//
//        Function<InventoryListing, String> l2picUrl = l2mpd.andThen(mpd2lmp).andThen(lmp2url);
//
//        Optional<String> url2 = Optional.ofNullable(listing)
//            .map(l -> l2picUrl(l));

    }


}
