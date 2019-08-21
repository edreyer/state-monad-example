package lambda;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.hlist.SingletonHList;
import com.jnape.palatable.lambda.adt.hlist.Tuple3;
import com.jnape.palatable.lambda.adt.hlist.Tuple4;
import com.jnape.palatable.lambda.adt.hmap.HMap;
import com.jnape.palatable.lambda.adt.hmap.TypeSafeKey;

import static com.jnape.palatable.lambda.adt.hlist.HList.HNil;
import static com.jnape.palatable.lambda.adt.hlist.HList.nil;
import static com.jnape.palatable.lambda.adt.hlist.HList.singletonHList;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.adt.hmap.TypeSafeKey.typeSafeKey;

public class HeterogeneousTypes {

    public static void hlistExamples() {

        // A heterogeneous list is typesafe on a per-slot basis
        HNil                   nil              = nil();
        SingletonHList<String> singleton        = singletonHList("singleton");
        SingletonHList<String> alsoSingleton    = nil.cons("also singleton");

        // Tuples are implemented as HLists, which gives tuples all HList APIs
        Tuple3<Float, String, Integer>      tuple3  = nil.cons(1).cons("two").cons(3f);
        Tuple4<Byte, Short, Integer, Long>  tuple4  = tuple((byte) 1, (short) 2, 3, 4L);
        Integer                             integer = tuple4._3();
        Tuple3<Short, Integer, Long>        tail    = tuple4.tail();

    }

    public static void hmapExamples() {

        HMap emptyHMap = HMap.emptyHMap();

        TypeSafeKey.Simple<String>  fooKey = typeSafeKey();
        TypeSafeKey.Simple<Integer> barKey = typeSafeKey();

        // This is compile-time type checked
        HMap updated = emptyHMap
            .put(fooKey, "string")
            .put(barKey, 1);

        boolean containsBar = updated.containsKey(barKey);

        Maybe<Integer>  maybeBar    = updated.get(barKey);
        String          fooOrError  = updated.demand(fooKey); // unsafe

    }
}
