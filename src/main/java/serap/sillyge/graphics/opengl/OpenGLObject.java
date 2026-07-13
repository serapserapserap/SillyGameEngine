package serap.sillyge.graphics.opengl;

import org.jetbrains.annotations.NotNull;

public interface OpenGLObject extends AutoCloseable {
    void init();
    int getIdentifier();
    @SuppressWarnings("all") //shut the FUCK up
    default boolean isInitialised() {
        return this.getIdentifier() != -1;
    }

    default boolean is(@NotNull OpenGLObject other) {
        return this.getIdentifier() == other.getIdentifier();
    }
}
