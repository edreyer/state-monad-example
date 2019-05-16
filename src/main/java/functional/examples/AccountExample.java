package functional.examples;

import functional.monad.Nothing;
import functional.monad.StateM;
import functional.monad.StateTuple;
import lombok.Value;

import static functional.monad.StateAPI.For;

@Value
class Account{
    private Integer balance;
}

class AccountS extends StateM<Account, Integer> {
    private AccountS(StateF<Account, Integer> run) { super(run); }

    public static AccountS deposit(int amount) {
        return new AccountS( a -> new StateTuple<>(
                a.getBalance() + amount,
                new Account(a.getBalance() + amount)
            )
        );
    }

    public static AccountS withdrawal(int amount) {
        return new AccountS( a -> new StateTuple<>(
                a.getBalance() - amount,
                new Account(a.getBalance() - amount)
            )
        );
    }
}

public class AccountExample {

    public static void main(String[] args) {
        StateM<Account, Nothing> program = For(
            AccountS.deposit(10),
            AccountS.deposit(100),
            AccountS.withdrawal(20),
            AccountS.withdrawal(20)).
            yield();

        Account newAccount = new Account(0);

        System.out.println(
            "The balance is: " + program.run.apply(newAccount).state.getBalance()
        );
        System.out.println(
            "The balance is: " + program.evalState(newAccount).getBalance()
        );

        // ---------------------------------

        // Broken out

        StateM.StateF runFunction = program.run;

        StateTuple<Integer, Account> state = (StateTuple<Integer, Account>)runFunction.apply(newAccount);

        Account theAccount = state.state;

        System.out.println("The Final Balance: " + theAccount.getBalance());

    }

}
