package serap.sillyge.io.resource.type;

import org.jetbrains.annotations.NotNull;

public enum Extensions implements Extension {
    PNG("png"),
    VERTEX_SHADER("vert"),
    FRAGMENT_SHADER("frag"),
    GLSL("glsl"),
    TEXT("txt"),
    JSON("json"),
    GLTF("gltf"),
    OBJ("obj");


    Extensions(@NotNull String ext) {
        this.ext = ext.toLowerCase();
    }

    private final String ext;

    @Override
    public String getExtension() {
        return this.ext;
    }
}
