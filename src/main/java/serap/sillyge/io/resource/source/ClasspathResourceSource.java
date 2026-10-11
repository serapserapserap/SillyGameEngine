package serap.sillyge.io.resource.source;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import serap.sillyge.io.resource.ResourceIdentifier;
import serap.sillyge.io.resource.accessor.ClasspathResourceAccessor;
import serap.sillyge.io.resource.accessor.ResourceAccessor;
import serap.sillyge.io.resource.type.Extension;

public record ClasspathResourceSource(ClassLoader targetClassLoader) implements ResourceSource {

    @NotNull
    @Contract("_ -> new")
    @Override
    public ResourceAccessor getAccessor(ResourceIdentifier identifier) {
        return new ClasspathResourceAccessor(identifier, this);
    }

    public boolean hasResource(@NotNull ResourceIdentifier identifier, Extension extension) {
        return ClasspathResourceSource.class.getResource(identifier.toClasspathString(extension)) != null;
    }
}
