package serap.sillyge.io.loaders.context;

import lombok.Getter;
import serap.sillyge.core.Registry;
import serap.sillyge.io.loaders.context.resourcereload.ResourceReloadContext;
import serap.sillyge.io.resource.ResourceIdentifier;

public abstract class LoaderContext<T, R extends Registry<T>> {
    @Getter
    private final R registry;
    @Getter
    private final ResourceReloadContext resourceLoadingContext;

    public LoaderContext(ResourceReloadContext loadingContext, R registry) {
        this.registry = registry;
        this.resourceLoadingContext = loadingContext;
    }

    public void register(T loadedResource, ResourceIdentifier identifier) {
        registry.register(identifier, loadedResource);
    }
}
