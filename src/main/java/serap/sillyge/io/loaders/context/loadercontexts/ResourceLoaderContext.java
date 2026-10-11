package serap.sillyge.io.loaders.context.loadercontexts;

import serap.sillyge.core.Registry;
import serap.sillyge.io.loaders.context.LoaderContext;
import serap.sillyge.io.loaders.context.resourcereload.ResourceReloadContext;
import serap.sillyge.io.resource.Resource;
import serap.sillyge.io.resource.ResourceIdentifier;
import serap.sillyge.io.resource.Unloadable;

public abstract class ResourceLoaderContext<T extends Unloadable> extends LoaderContext<Resource<T>, Registry.Mutable<T, Resource<T>>> {
    public ResourceLoaderContext(ResourceReloadContext loadingContext, Registry.Mutable<T, Resource<T>> registry) {
        super(loadingContext, registry);
    }

    public void deposit(T loadedResource, ResourceIdentifier identifier) {
        this.getRegistry().deposit(identifier, loadedResource);
    }
}
