package serap.sillyge.io.resource;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import serap.sillyge.core.pointer.MutablePointer;
import serap.sillyge.io.resource.type.ResourceType;

import java.util.Arrays;

public class Resource<T extends Unloadable> implements MutablePointer<T> {

    //the stored resource
    protected T resource = null;
    //the type
    protected ResourceType<?> type;
    //the resources that this depends on
    protected Resource<?>[] dependencies = null;

    /**
     * creates a new resource
     * @param type the type of the resource
     * @param initialValue the value that the resource should hold before it's actual value is loaded
     */
    protected Resource(@NotNull ResourceType<?> type, @Nullable T initialValue) {
        this.resource = initialValue;
    }

    /**
     * creates a new resource
     * @param type the type of the resource
     */
    protected Resource(@NotNull ResourceType<?> type) {
        this(type, null);
    }

    /**
     * @return {@code true} if the resource depends on other resources to work, {@code false} if it doesn't
     */
    public boolean hasDependencies() {
        return this.dependencies != null;
    }

    /**
     * updates the resource in use
     * @param newResource the resource to start using
     */
    protected void updateResource(T newResource) {
        this.resource = newResource;
    }

    /**
     * marks the resource as depending on another resource
     * @param resource the resource that this resource depends on
     */
    public void dependsOn(Resource<?> resource) {
        if(!this.hasDependencies()) {
            this.dependencies = new Resource<?>[1];
            this.dependencies[0] = resource;
            return;
        }

        int putIndex = this.dependencies.length;
        this.dependencies = Arrays.copyOf(
                this.dependencies,
                putIndex + 1
        );
        this.dependencies[putIndex] = resource;
    }

    @Override
    @Nullable
    public T get() {
        return this.resource;
    }

    @Override
    public void pointTo(T neu) {
        this.resource = neu;
    }
}
