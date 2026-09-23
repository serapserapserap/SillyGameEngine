package serap.sillyge.io.resource;

import org.jetbrains.annotations.NotNull;

public enum ResourceType {
    TEXTURE("png"),
    TTF("ttf");

    private final String targetFileExtension;

    ResourceType(String targetFileExtension) {
        this.targetFileExtension = targetFileExtension;
    }

    @NotNull
    public static ResourceType extrapolateFromPathString(String pathString) {
        int lastDotIndex = pathString.lastIndexOf('.');
        String extension = pathString.substring(lastDotIndex + 1).toLowerCase();
        for (ResourceType value : ResourceType.values()) {
            if(value.targetFileExtension.equals(extension)) {
                return value;
            }
        }

        throw new UnsupportedResourceException("Cannot extrapolate resource type for the file extension \"" + extension + "\" as it is not supported" );
    }
}
