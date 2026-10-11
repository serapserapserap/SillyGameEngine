package serap.sillyge.io.resource.accessor;

import org.jetbrains.annotations.NotNull;
import serap.sillyge.io.resource.ResourceIdentifier;
import serap.sillyge.io.resource.source.ClasspathResourceSource;
import serap.sillyge.io.resource.type.Extension;

import java.io.InputStream;

public class ClasspathResourceAccessor implements ResourceAccessor {
    private final ResourceIdentifier identifier;
    private final ClassLoader targetClassLoader;

    private ClasspathResourceAccessor(ResourceIdentifier identifier, ClassLoader classLoader) {
        this.identifier = identifier;
        this.targetClassLoader = classLoader;
    }

    public ClasspathResourceAccessor(ResourceIdentifier identifier, @NotNull ClasspathResourceSource source) {
        this(identifier, source.targetClassLoader());
    }

    @Override
    public InputStream getAsStream(Extension extension) {
        return this.targetClassLoader.getResourceAsStream(this.identifier.toClasspathString(extension));
    }

    @Override
    public ResourceIdentifier getIdentifier() {
        return this.identifier;
    }
}
