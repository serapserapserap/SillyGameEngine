package serap.sillyge.io.resource;


public enum ResourceType {
    TEXTURE("png"),
    TTF("ttf");

    private final String targetFileExtension;

    ResourceType(String targetFileExtension) {
        this.targetFileExtension = targetFileExtension;
    }

    /*
     * todo -> provide actual fallbacks for all resource type cases
     *  the goal of the fallback system is so that if something fails to load it won't nullpointer, it will just log an error
     * and render using a fallback texture
     */
    public static Object getFallback(ResourceType type) {
        return null;
    }

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
