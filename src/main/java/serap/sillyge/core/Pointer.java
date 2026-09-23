package serap.sillyge.core;

import java.util.concurrent.ExecutionException;

public interface Pointer<T> {
    T get();
    void set(T value);

    static <T> Pointer<T> newPointer() {
        return null;
    }

    static <T> T unwrap(Pointer<T> pointer) {
        return pointer.get();
    }
}

