package lambda;

import java.io.Serializable;
import java.util.List;

import com.jnape.palatable.lambda.adt.coproduct.CoProduct2;

public class CoProductExample {
    /*
        Problem: List could contain any Seriaizable. Runtime error.
     */
    public void process(List<Serializable> items) {
        for (Serializable item : items) {
            Integer value;
            if (item instanceof String) {
                value = ((String) item).length();
            } else if (item instanceof Integer) {
                value = (Integer) item;
            } else {
                throw new IllegalArgumentException("only strings and ints allowed");
            }
            // do something with value
            System.out.println(value);
        }
    }

    /*
        Benefit: Compile time enforcement that only String, Integer supported
     */
    public void process2(List<CoProduct2<String, Integer, ?>> items) {
        for (CoProduct2<String, Integer, ?> item : items) {
            Integer value = item.match(String::length, integer -> integer);

            // .. do something with value
            System.out.println(value);
        }
    }

}
