package serap.sillyge.core.pointer;

import serap.sillyge.core.Pointer;

import java.util.concurrent.ExecutionException;

public class SimplePointer<T> implements MutablePointer<T> {

    protected T value;

    @Override
    public T get() {
        return this.value;
    }

    @Override
    public void set(T value) {
        this.value = value;
    }
}
