package serap.sillyge.io.resource;

import serap.sillyge.core.Pointer;

public class BufferedResource<T> implements Pointer<T> {

    protected T primary;
    protected T fallback;
    protected ResourceType type;
    protected String path;

    public static <T> BufferedResource<T> of(String path) {
        ResourceType type = ResourceType.extrapolateFromPathString(path);
        return new 
    }

    private BufferedResource(ResourceManager resourceManager, String path, ResourceType type, T initialFallback) {
        this.path = path;
        this.fallback = initialFallback;
    }

    @Override
    public T get() {
        return null;
    }

    @Override
    public void set(T value) {
        throw new IllegalStateException("BufferedResource can only be modified by changing it's resource location");
    }
}
