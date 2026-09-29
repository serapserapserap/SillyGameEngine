package serap.sillyge.graphics.opengl.program.shader;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import serap.sillyge.graphics.opengl.IntegerIdentifiable;
import serap.sillyge.graphics.opengl.OpenGL;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader implements IntegerIdentifiable {
    private int identifier = -1;
    @Getter
    private final OpenGL.ShaderType type;
    @Setter
    private String source;


    public Shader(@NotNull OpenGL.ShaderType type) {
        this(type, null);
    }

    public Shader(@NotNull OpenGL.ShaderType type, @Nullable String source) {
        this.type = type;
        this.source = source;
    }

    public void createShader(@NotNull String source, boolean discardSource) {
        this.source = source;
        this.createShader(discardSource);
    }

    public void createShader(boolean discardSource) {
        glShaderSource(this.identifier, this.source);
        glCompileShader(this.identifier);
        if(discardSource) this.source = null;
    }

    @Nullable
    public String getShaderCreationErrorLog() {
        int[] success = new int[1];
        glGetShaderiv(this.identifier, GL_COMPILE_STATUS, success);
        if(success[0] == GL_FALSE) {
            String logs = glGetShaderInfoLog(this.identifier);
            return String.format("failed to create shader of type %s: %s", this.type, logs);
        }
        return null;
    }

    public void init() {
        this.identifier = glCreateShader(this.type.identifier);
    }

    @Override
    public int getIdentifier() {
        return this.identifier;
    }

    @Override
    public void close() {
        if(this.identifier != -1) {
            glDeleteShader(this.identifier);
            this.identifier = -1;
        }
    }
}
