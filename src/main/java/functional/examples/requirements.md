## Random Number Generator Requirements

1. Generated numbers should be evenly distributed over a range
1. Should be able to reproduce the series, for testing. (Not truly random).

### Pure Functions

* Single input, Single return value
* No side effects
* Always returns the same value for a given input.

### java.util.Random

* Has `nextInt()` method (among others) which changes its internal mutable state
* Therefore, not functional, not pure
* Can be initialized with a seed value (allowing a series to be reproduced)
    * default seed value depends on System clock.

### Towards a functional Random number generator

* What might the API look like? 
* We're going to use the ability to seed `java.util.Random` to our advantage

```java
class RandomUtils {
    public Tuple2<Integer, Seed> nextInt(Seed seed) {...}
}
```

Where:

* `Tuple2` is just a container for two values (similar to `com.cargurus.util.Pair`)
* `Seed` is an immutable container for an int used to seed Random

Each time we obtain a new random integer, we also obtain the state (`Seed`) required to 
generate the next integer.


```java
@Value // <- Lombok annotation for immutable type
class Seed {
    private int seed;
}
```

---

Our random int generating function looks like this:

```java
Tuple2<Integer, Seed> nextInt(Seed seed) {
    Random r = seed == null               // null for unpredicable results
        ? new Random() 
        : new Random(seed.getSeed());
    
    int i = r.nextInt();                  // next random integer    
    return new Tuple2<>(i, new Seed(i));  // return random int and new `Seed`
}
```

Notice this method is private.  All we need to now is a Generator:

```java
class Generator1 {
    // Concrete API
    public static Tuple2<Integer, Seed> nextInt(Seed seed) {
        return RandomUtils.nextInt(seed);
    }
}
```

Putting it all together

```java
import static Generator1.nextInt;

Seed seed = new Seed(0);

Tuple2<Integer, Seed> t1 = nextInt(seed);
Tuple2<Integer, Seed> t2 = nextInt(t1._2());
Tuple2<Integer, Seed> t3 = nextInt(t2._2());

assertEquals(Integer.valueOf(-1155484576), t1._1());
assertEquals(Integer.valueOf(-1764305998), t2._1());
assertEquals(Integer.valueOf(-131000125), t3._1());

// same input, same output
assertEquals(Integer.valueOf(-131000125), nextInt(t2._2())._1());
assertEquals(Integer.valueOf(-131000125), nextInt(t2._2())._1());
assertEquals(Integer.valueOf(-131000125), nextInt(t2._2())._1());

```

###But wait, there's more...

Our function signature of 

`Tuple2<Integer, Seed> nextInt(Seed seed)`

can be expressed as

`Function<Seed, Tuple2<Integer, Seed>>`

But if you want implementations that go beyond Integer...

`Function<Seed, Tuple<A, Seed>>`

This allows us to create new capabilities to produce other types

`Function<Seed, Tuple2<Boolean, Seed>> // for random Boolean` 

But it's annoying to have to read function signatures like this,
so let's "alias" it by extending the function

`public interface RandomF<A> extends Function<Seed, <Tuple<A, Seed>>`

this allows you to replace...

```
public static Tuple2<Integer, Seed> integer(Seed seed) {
    return rng.nextInt()
}
```

with...

```
public static Random<Integer> integer = RandomUtils::nextInt
```

Usage looks very similar.  Notice the

```java
import static Generator2.nextInt;

Seed seed = new Seed(0);

Tuple2<Integer, Seed> t1 = nextInt.apply(seed);
Tuple2<Integer, Seed> t2 = nextInt.apply(t1._2());
Tuple2<Integer, Seed> t3 = nextInt.apply(t2._2());
```

Guess what?  We can generalize even further

`Function<Seed, Tuple<A, Seed>>`


becomes

```
Function<S, Tuple2<A, S>>
```

which can be aliased using...

```
interface StateM<S, A> extends Function<S, Tuple2<A, S>>
```

or because composition is better generally better than inheritance

```java
public class StateM<S, A> {

    public interface StateF<S,A> extends Function<S, Tuple2<A, S>> {};

    public final StateF<S,A> run;

    public StateM(StateF<S,A> run) {
        this.run = run;
    }
    // ...
}
```

We've just discovered the StateM Monad.  All we have to do is add `unit` 
and `flatMap` methods (plus a few other convenience methods).

* General purpose computation utility that allows state to be carried
* Composable into more complex operations 

"The goal and purpose of the State monad is to compose functions, 
and eventually unleash a potentially very complex chain of composed behavior 
on some externally provided stateful entity. 

There is no state in StateM because monad-nature is not about state, 
but rather it is about how to compose behavior into something that may 
behave like state."

---

The final shape of our solution looks like this and usage is very similar to the 
others.

```java
public static class RandomS<A> extends StateM<Seed, A> {

    private RandomS(StateF<Seed, A> run) {
        super(run);
    }

    public static RandomS<Integer> intRnd = new RandomS<>(RandomUtils::nextInt);

}


public static class Generator3 {
    StateM
    public static RandomS<Integer> nextInt = RandomS.intRnd;
}
```

How else can we use the StateM Monad?

TODO: Fibonacci using For comprehension