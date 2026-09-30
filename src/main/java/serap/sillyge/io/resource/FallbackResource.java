package serap.sillyge.io.resource;

import org.jetbrains.annotations.Nullable;
import serap.sillyge.core.Pointer;

import java.util.Objects;

public class FallbackResource<T> implements Pointer<T> {

    protected T primary = null;
    protected T fallback;

    protected FallbackResource(@Nullable T initialFallback) {
        this.fallback = initialFallback;
    }

    protected void updatePrimary(T newPrimary) {
        this.fallback = this.primary;
        this.primary = newPrimary;
    }

    @Override
    @Nullable
    public T get() {
        return Objects.requireNonNullElse(this.primary, this.fallback);
    }
}
