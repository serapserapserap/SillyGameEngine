package serap.sillyge.io.resource;

import org.jetbrains.annotations.NotNull;
import serap.sillyge.SillyGameEngine;
import serap.sillyge.io.resource.type.Extension;

import java.io.File;
import java.nio.file.Path;

public class ResourceIdentifier {

    public static ResourceIdentifier engineIdentifierOf(String identifier) {
        return new ResourceIdentifier(SillyGameEngine.ENGINE_NAME_SHORT, identifier);
    }

    private final String resourceNamespace;
    private final String resourceIdentifier;

    public String getNamespacedIdentifier() {
        return String.format("%s::%s", this.resourceNamespace, this.resourceIdentifier);
    }

    public ResourceIdentifier(String namespace, String identifier) {
        this.resourceNamespace = namespace;
        this.resourceIdentifier = identifier.toLowerCase();
    }

    public String getIdentifier() {
        return this.resourceIdentifier;
    }

    public String getNamespace() {
        return this.resourceNamespace;
    }

    @Override
    public boolean equals(Object other) {
        if(other == this) return true;

        if(other instanceof ResourceIdentifier otherResourceIdentifier) {
            return this.resourceNamespace.equals(otherResourceIdentifier.resourceNamespace) && this.resourceIdentifier.equals(otherResourceIdentifier.resourceIdentifier);
        }

        return false;
    }

    public String toClasspathString() {
        return this.getIdentifier().replace(".", "/");
    }

    public String toClasspathString(@NotNull Extension ext) {
        return this.toClasspathString() + "." + ext.getExtension();
    }

    public String toPathString() {
        return this.getIdentifier().replace(".", File.pathSeparator);
    }

    public String toPathString(@NotNull Extension ext) {
        return this.toPathString() + "." + ext.getExtension();
    }

    public Path asPath(Path source) {
        String[] subPaths = this.getIdentifier().split(".");

        for (String subPath : subPaths) {
            source = source.resolve(subPath);
        }

        return source;
    }

    public Path asPath(Path source, @NotNull Extension extension) {
        return this.asPath(source).resolve(extension.getExtension());
    }

    @Override
    public String toString() {
        return String.format(
                "ResourceIdentifier[namespace=%s, identifier=%s]",
                this.resourceNamespace,
                this.resourceIdentifier
        );
    }
}
