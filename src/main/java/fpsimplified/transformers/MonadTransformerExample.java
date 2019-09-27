package fpsimplified.transformers;

import io.vavr.concurrent.Future;
import io.vavr.control.Option;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$None;
import static io.vavr.Patterns.$Some;

public class MonadTransformerExample {

    class User {}
    class Address {}

    abstract class Step1 {

        abstract Future<User> findById(Long userId);
        abstract Future<Address> findAddressByUser(User user);

        // Yay, this looks great
        // But what if there isn't a corresponding user for every userId?
        // See Step2
        Future<Address> findAddressByUserId(Long userId) {
            return findById(userId)
                .flatMap(this::findAddressByUser);
        }
    }

    abstract class Step2 {

        abstract Future<Option<User>> findById(Long userId);
        abstract Future<Option<Address>> findAddressByUser(User user);

        // Uh Oh,
        // Error:(36, 26) java: incompatible types: invalid method reference
        // incompatible types: io.vavr.control.Option<fpsimplified.transformers.MonadTransformerExample.User>
        // cannot be converted to fpsimplified.transformers.MonadTransformerExample.User
//        Future<Option<Address>> findAddressByUserId(Long userId) {
//            return findById(userId)
//                // Expects a User, but given Option<User>
//                .flatMap(this::findAddressByUser);
//        }

        // maybe this instead?
        // Better, but arguably ugly.
        // What we really want is a sort of double flatMap
        Future<Option<Address>> findAddressByUserIdTake2(Long userId) {
            return findById(userId)
                // Expects a User, but given Option<User>
                .flatMap(userOp -> Match(userOp).of(
                    Case($Some($()), user -> findAddressByUser(user)),
                    Case($None(), Future.successful(Option.none()))
                ));
        }

    }
}
