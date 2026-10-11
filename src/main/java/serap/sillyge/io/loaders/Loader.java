package serap.sillyge.io.loaders;

import org.jetbrains.annotations.NotNull;
import serap.sillyge.io.loaders.context.LoaderContext;
import serap.sillyge.io.loaders.context.resourcereload.ResourceReloadContext;
import serap.sillyge.io.resource.ResourceIdentifier;
import serap.sillyge.io.resource.source.ResourceSource;
import serap.sillyge.io.resource.type.Extension;

import java.io.IOException;
import java.io.InputStream;

public abstract class Loader<C extends LoaderContext<?, ?>> {
    protected Loader(C context) {
        this.loaderContext = context;
    }

    private final C loaderContext;

    protected C getLoaderContext() {
        return this.loaderContext;
    }

    protected ResourceReloadContext getResourceLoadingContext() {
        return this.loaderContext.getResourceLoadingContext();
    }

    protected abstract void loadFromStream(@NotNull InputStream stream);

    public void openAs(@NotNull ResourceSource source, @NotNull ResourceIdentifier identifier, @NotNull Extension extension) throws IOException {
        try(InputStream stream = source.getAccessor(identifier).getAsStream(extension)) {
            this.loadFromStream(stream);
        }
    }

    protected void loadResourcesRecursively() {

    }
}
