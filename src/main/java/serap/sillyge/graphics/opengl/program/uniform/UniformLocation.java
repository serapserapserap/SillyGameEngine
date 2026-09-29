package serap.sillyge.graphics.opengl.program.uniform;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import serap.sillyge.core.Pointer;
import serap.sillyge.graphics.opengl.program.Program;

import java.util.function.Supplier;

public final class UniformLocation {
    private final static ObjectArrayList<UniformLocation> uniformLocations = new ObjectArrayList<>();

    public static void refreshLocations() {
        for (UniformLocation uniformLocation : UniformLocation.uniformLocations) {
            uniformLocation.refreshLocation();
        }
    }

    public UniformLocation(@NotNull final String uniformName, @NotNull final Pointer<Program> parentProgram) {
        if(!uniformLocations.add(this)) {
            throw new IllegalStateException("??? location already exists");
        }
        this.getLocationFunc = () -> parentProgram.get().getUniform(uniformName);
    }

    private void refreshLocation() {
        this.location = this.getLocationFunc.get();
    }

    public final Supplier<Integer> getLocationFunc;
    private int location;

    public int location() {
        return this.location;
    }
}

