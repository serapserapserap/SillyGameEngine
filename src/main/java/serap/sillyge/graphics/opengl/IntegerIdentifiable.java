package serap.sillyge.graphics.opengl;

import org.jetbrains.annotations.NotNull;

public interface IntegerIdentifiable extends AutoCloseable {
    int getIdentifier();
    @SuppressWarnings("all") //shut the FUCK up
    default boolean isInitialised() {
        return this.getIdentifier() != -1;
    }

    default boolean is(@NotNull IntegerIdentifiable other) {
        return this.getIdentifier() == other.getIdentifier();
    }
    default boolean is(@NotNull LongIdentifiable other) {
        return this.getIdentifier() == other.getIdentifier();
    }
    default boolean is(long id) {
        return this.getIdentifier() == id;
    }
    default boolean is(int id) {
        return this.getIdentifier() == id;
    }
}
