package functional.examples;

import functional.state.Condition;
import functional.state.Input;
import functional.state.StateMachine;
import functional.state.Transition;
import functional.state.ConditionTransition;
import io.vavr.collection.List;
import lombok.Value;

public class StateMachineExample {

    public interface AtmInput extends Input {
        Type type();
        boolean isDeposit();
        boolean isWithdrawal();
        int getAmount();
        enum Type {Deposit, Withdrawal};
    }

    @Value
    public static class Deposit implements  AtmInput {
        private final int amount;

        @Override public Type type() {
            return Type.Deposit;
        }

        @Override public boolean isDeposit() {
            return true;
        }

        @Override public boolean isWithdrawal() {
            return false;
        }
    }

    @Value
    public static class Withdrawal implements AtmInput {
        private final int amount;

        @Override public Type type() {
            return Type.Withdrawal;
        }

        @Override public boolean isDeposit() {
            return false;
        }

        @Override public boolean isWithdrawal() {
            return true;
        }
    }

    /**
     *  represents the result Tuple
     */
    @Value
    public static class Outcome {
        final Integer account;
        final List<Integer> operations;
    }

    public static class ATM {
        public static StateMachine<AtmInput, Outcome> createMachine() {

            Condition<AtmInput, Outcome> depositTest = t -> t.value.isDeposit();
            Transition<AtmInput, Outcome>depositTrans = t -> new Outcome(
                t.state.account + t.value.getAmount(),
                t.state.operations.prepend(t.value.getAmount())
            );

            Condition<AtmInput, Outcome> withdrawalTest = t -> t.value.isWithdrawal();
            Transition<AtmInput, Outcome>withdrawalTrans = t -> new Outcome(
                t.state.account - t.value.getAmount(),
                t.state.operations.prepend(-t.value.getAmount())
            );

            // terminal condition
            Condition<AtmInput, Outcome> lastTest = t -> true;
            Transition<AtmInput, Outcome>lastTrans = t -> t.state;

            List<ConditionTransition<AtmInput, Outcome>>
                transitions = List.of(
                    new ConditionTransition(depositTest, depositTrans),
                    new ConditionTransition(withdrawalTest, withdrawalTrans),
                    new ConditionTransition(lastTest, lastTrans)
                );

            return new StateMachine<>(transitions);
        }
    }

    public static void main(String[] args) {

        List<AtmInput> inputs = List.of(
            new Deposit(200),
            new Withdrawal(50),
            new Withdrawal(50),
            new Withdrawal(50),
            new Deposit(200)
        );

        Outcome result = ATM.createMachine()
            .process(inputs)
            .eval(new Outcome(0, List.empty()));

        System.out.println(result);
    }
}
