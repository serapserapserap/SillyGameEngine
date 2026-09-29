package serap.sillyge.core.pointer;

import serap.sillyge.core.Pointer;

public interface MutablePointer<T> extends Pointer<T> {
    void set(T neu);
}
