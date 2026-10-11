package serap.sillyge.io.resource.exception;

import serap.sillyge.io.resource.ResourceIdentifier;

public final class ResourceLoadException extends RuntimeException {
    public ResourceLoadException(ResourceIdentifier identifier, Exception e, boolean isOnRenderThread) {
        super("Error loading " + identifier.getNamespacedIdentifier() + (isOnRenderThread ? " on render thread" : " on worker thread"), e);
    }
}
