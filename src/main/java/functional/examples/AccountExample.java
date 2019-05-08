package functional.examples;

import functional.monad.StateM;
import io.vavr.Tuple2;
import lombok.Value;

import static functional.monad.StateAPI.For;

@Value
class Account{
    private Integer balance;
}

class AccountS extends StateM<Account, Integer> {
    private AccountS(StateF<Account, Integer> run) { super(run); }

    public static AccountS deposit(int amount) {
        return new AccountS(
            (Account a) -> new Tuple2<>(amount, new Account(a.getBalance() + amount))
        );
    }

    public static AccountS withdrawal(int amount) {
        return new AccountS(
            (Account a) -> new Tuple2<>(amount, new Account(a.getBalance() - amount))
        );
    }
}

public class AccountExample {

    public static void main(String[] args) {
        StateM<Account, Integer> program = For(
            AccountS.deposit(10),
            AccountS.deposit(100),
            AccountS.withdrawal(20),
            AccountS.withdrawal(20)).
            yield((x, y) -> 0);

        Account newAccount = new Account(0);

        System.out.println(
            "The balance is: " + program.run.apply(newAccount)._2.getBalance()
        );
        System.out.println(
            "The balance is: " + program.evalState(newAccount).getBalance()
        );

        // ---------------------------------

        // Broken out

        StateM.StateF runFunction = program.run;

        Tuple2<Integer, Account> state = (Tuple2<Integer, Account>)runFunction.apply(newAccount);

        Account theAccount = state._2;

        System.out.println("The Final Balance: " + theAccount.getBalance());

    }

}
