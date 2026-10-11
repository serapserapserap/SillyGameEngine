package serap.sillyge.io.resource.type;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import serap.sillyge.io.resource.ResourceIdentifier;
import serap.sillyge.io.resource.ResourceRegistry;
import serap.sillyge.io.loaders.Loader;

public class ResourceType<T extends Loader> {
    public static void registerType(ResourceType<?> type) {
        ResourceRegistry.TYPE_REGISTRY.register(type.identifier, type);
    }

    @Getter
    private final ResourceIdentifier identifier;
    private final Extension[] extensions;
    @Getter
    private final T resourceLoader;

    public ResourceType(ResourceIdentifier identifier, T resourceLoader, @NotNull Extension... validExtensions) {
        this.identifier = identifier;
        this.extensions = validExtensions;
        this.resourceLoader = resourceLoader;
    }


    @NotNull
    protected static String cleanExtension(@NotNull String extension) {
        return extension.replace(".", "").toLowerCase();
    }

    public boolean isAllowedFile(@NotNull String fileNameAndExtension) {
        return isAllowedFileExtension(fileNameAndExtension.substring(fileNameAndExtension.lastIndexOf('.')));
    }

    public boolean isAllowedFileExtension(String extension) {
        extension = ResourceType.cleanExtension(extension);
        for (Extension allowedExtension : this.extensions) {
            if(allowedExtension.getExtension().equals(extension)) {
                return true;
            }
        }
        return false;
    }
}
