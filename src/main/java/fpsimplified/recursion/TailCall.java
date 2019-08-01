package fpsimplified.recursion;

import java.util.function.Supplier;

/**
 * Utility class that allows you to define recursive functions that use the Heap instead of the Stack
 * for frame allocation.
 *
 * @param <T>
 */
public abstract class TailCall<T> {

    public static <T> Return<T> ret(T t) {
        return new Return<>(t);
    }

    public static <T> Suspend<T> sus(Supplier<TailCall<T>> supplier) {
        return new Suspend<>(supplier);
    }

    public abstract TailCall<T> resume();

    public abstract T eval();

    public abstract boolean isSuspend();

    private TailCall() { }


    public static class Return<T> extends TailCall<T> {

        private final T t;

        public Return(T t) {
            this.t = t;
        }

        @Override
        public TailCall<T> resume() {
            throw new IllegalStateException("Return has no resume");
        }

        @Override
        public T eval() {
            return t;
        }

        @Override
        public boolean isSuspend() {
            return false;
        }
    }


    public static class Suspend<T> extends TailCall<T> {

        private final Supplier<TailCall<T>> resume;

        public Suspend(Supplier<TailCall<T>> resume) {
            this.resume = resume;
        }

        @Override public TailCall<T> resume() {
            return resume.get();
        }

        @Override public T eval() {
            TailCall<T> tailRec = this;
            while(tailRec.isSuspend()) {
                tailRec = tailRec.resume();
            }
            return tailRec.eval();
        }

        @Override public boolean isSuspend() {
            return true;
        }
    }

}
