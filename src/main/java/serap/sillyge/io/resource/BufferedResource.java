package serap.sillyge.io.resource;

import org.jetbrains.annotations.NotNull;
import serap.sillyge.core.Managers;
import serap.sillyge.core.Pointer;

import java.util.Objects;

public class BufferedResource<T> implements Pointer<T> {

    protected T primary;
    protected T fallback;
    protected ResourceType type;
    protected ResourceIdentifier identifier;

    @SuppressWarnings("unchecked")
    public static <T> BufferedResource<T> of(@NotNull ResourceIdentifier identifier) {
        ResourceType type = ResourceType.extrapolateFromPathString(identifier.getIdentifier());
        return new BufferedResource<>(
                Managers.getResourceManager(),
                identifier,
                type,
                (T) ResourceType.getFallback(type)
        );
    }

    private BufferedResource(@NotNull ResourceRegistry resourceRegistry, @NotNull ResourceIdentifier resourceIdentifier, @NotNull ResourceType type, @NotNull T initialFallback) {
        this.identifier = resourceIdentifier;
        this.fallback = initialFallback;
    }

    @Override
    public T get() {
        return Objects.requireNonNullElse(this.primary, this.fallback);
    }
}
